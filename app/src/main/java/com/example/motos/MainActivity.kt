package com.example.motos

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.motos.databinding.ActivityMainBinding
import com.example.motos.utils.SessionManager
import com.example.motos.view.activity.LoginActivity
import com.example.motos.view.fragment.HistorialClientesFragment
import com.example.motos.view.fragment.InicioFragment
import com.example.motos.view.fragment.MotoSegundaManoFragment
import com.example.motos.view.fragment.PerfilFragment
import com.example.motos.view.fragment.ReservasFragment
import com.example.motos.view.fragment.TallerFragment
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locale = Locale("es", "ES") //fuerza a que se ponga en ESP
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        // Cargar fragment inicial
        if (savedInstanceState == null) {

            val session = SessionManager(this)
            val fragment = InicioFragment().apply { // pasamos valores para el nombre de usuario al loguearse
                arguments = Bundle().apply {
                    putString("username", session.getUsername() ?: "Invitado")
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, InicioFragment())
                .commit()
        }

        setupBottomNav()
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, InicioFragment())
                        .commit()
                    true
                }
                R.id.nav_motos -> {
                    // TODO: MotoFragment
                    true
                }
                R.id.nav_segunda -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, MotoSegundaManoFragment())
                        .commit()
                    true
                }
                R.id.nav_taller -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, TallerFragment())
                        .commit()
                    true
                }
                R.id.nav_ajustes -> {
                    showSettingsMenu()
                    false
                }
                else -> false
            }
        }
    }

    private fun showSettingsMenu() {
        val rol = session.getRol() ?: "INVITADO"
        android.util.Log.d("ROL_DETALLE", "Rol recibido: '$rol'")
        val anchor = binding.bottomNav.findViewById<View>(R.id.nav_ajustes)

        val popup = PopupMenu(this, anchor)

        popup.menu.add(0, 1, 0, "Perfil")

        if (rol == "CLIENTE") {
            popup.menu.add(0, 2, 1, "Mis reservas")
            popup.menu.add(0, 3, 2, "Mis Citas")

        }
        if (rol == "MECANICO") {
            popup.menu.add(0, 4, 3, "Solicitudes de Reparaciones")
            popup.menu.add(0, 7, 7, "Historial clientes")

        }
        if (rol == "VENDEDOR") {
            popup.menu.add(0, 5, 4, "Solicitudes de Reservas")
        }
        if( rol =="ADMIN"){
            popup.menu.add(0, 2, 1, "Mis reservas")
            popup.menu.add(0, 3, 2, "Citas")
            popup.menu.add(0, 4, 3, "Reparaciones")
            popup.menu.add(0, 7, 7, "Historial clientes")

            popup.menu.add(0, 5, 4, "Solicitudes de reservas")
            popup.menu.add(0, 6, 6, "Crear usuario")

        }

        popup.menu.add(0, 99, 99, "Cerrar sesión")

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> { supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, PerfilFragment())
                    .addToBackStack(null)
                    .commit()
                    true }
                2 -> {  supportFragmentManager.beginTransaction() //reservas cliente
                    .replace(R.id.fragmentContainer, ReservasFragment())
                    .addToBackStack(null)
                    .commit()
                    true }
                3 -> { supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, TallerFragment())
                    .addToBackStack(null)
                    .commit()
                    true }
                4 ->{ supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, TallerFragment())
                    .addToBackStack(null)
                    .commit()
                    true}
                5 -> {
                    // Solicitudes de reservas
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ReservasFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                6 ->{
                    mostrarDialogoCrearUsuario()

                    true
                }
                7 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HistorialClientesFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                99 -> {
                    session.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }



    private fun mostrarDialogoCrearUsuario() {
        val dialogBinding = com.example.motos.databinding.DialogCrearUsuarioBinding.inflate(layoutInflater)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Crear") { _, _ ->
                val username = dialogBinding.etUsername.text.toString().trim()
                val password = dialogBinding.etPassword.text.toString().trim()
                val rol = if (dialogBinding.rbVendedor.isChecked) "VENDEDOR" else "MECANICO"

                if (username.isEmpty() || password.isEmpty()) {
                    android.widget.Toast.makeText(this, "Completa los campos", android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                crearUsuario(username, password, rol)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun crearUsuario(username: String, password: String, rol: String) {
        lifecycleScope.
        launch {
            try {
                val api = com.example.motos.network.RetrofitClient.getInstance(this@MainActivity)
                val response = api.register(
                    com.example.motos.model.RegisterRequest(username, password, rol)
                )
                if (response.isSuccessful) {
                    android.widget.Toast.makeText(
                        this@MainActivity,
                        "Usuario $rol creado correctamente",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                } else {
                    android.widget.Toast.makeText(
                        this@MainActivity,
                        "Error: ${response.code()}",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() { // hace que se desloguee al cerrar app, si se rota pantalla no cierra
        super.onDestroy()
        if (isFinishing) {
            session.logout()
        }
    }
}