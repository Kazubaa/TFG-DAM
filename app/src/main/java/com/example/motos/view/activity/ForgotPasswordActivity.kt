package com.example.motos.view.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.motos.databinding.ActivityForgotPasswordBinding
import com.example.motos.model.ForgotPasswordRequest
import com.example.motos.network.RetrofitClient
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        binding.btnEnviar.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()

            if (username.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            enviarReset(username, email)
        }
    }

    private fun enviarReset(username: String, email: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnEnviar.isEnabled = false

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getInstance(this@ForgotPasswordActivity)
                val response = api.forgotPassword(ForgotPasswordRequest(username, email))
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Si los datos son correctos, recibirás un correo con instrucciones",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Error: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnEnviar.isEnabled = true
            }
        }
    }
}