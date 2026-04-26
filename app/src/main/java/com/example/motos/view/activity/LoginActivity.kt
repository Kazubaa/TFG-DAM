package com.example.motos.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.motos.MainActivity
import com.example.motos.databinding.ActivityLoginBinding
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.AuthRepository
import com.example.motos.utils.SessionManager
import com.example.motos.viewmodel.AuthState
import com.example.motos.viewmodel.AuthViewModel
import com.example.motos.viewmodel.AuthViewModelFactory
import com.google.android.material.tabs.TabLayout
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager
    private var isLoginMode = true

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.getInstance(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locale = Locale("es", "ES")
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        android.util.Log.d("LOGIN", "isLoggedIn=${session.isLoggedIn()} username=${session.getUsername()} token=${session.getToken()}")

        if (session.isLoggedIn()) {
            goToMain()
            return
        }

        setupTabs()
        setupListeners()
        observeViewModel()
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Iniciar sesión"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Registrarse"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                isLoginMode = tab.position == 0
                binding.btnAction.text = if (isLoginMode) "ENTRAR" else "REGISTRARSE"
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupListeners() {
        binding.btnAction.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isLoginMode) viewModel.login(username, password)
            else viewModel.register(username, password)
        }

        binding.tvGuest.setOnClickListener {
            goToMain(isGuest = true)
        }
    }

    private fun observeViewModel() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnAction.isEnabled = false
                }
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnAction.isEnabled = true
                    session.logout() //limpia sesión anterior
                    session.saveToken(state.data.token)
                    session.saveRol(state.data.rol)
                    session.saveUsername(state.data.username)
                    session.saveId(state.data.id)
                    state.data.clienteId?.let { session.saveClienteId(it) }
                    state.data.mecanicoId?.let { session.saveMecanicoId(it) }
                    state.data.vendedorId?.let { session.saveVendedorId(it) }
                    goToMain()
                }
                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnAction.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun goToMain(isGuest: Boolean = false) {
        if (isGuest) {
            session.logout()
            session.saveRol("INVITADO")
            session.saveUsername("Invitado")
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}