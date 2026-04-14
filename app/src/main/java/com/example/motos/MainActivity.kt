package com.example.motos

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.motos.databinding.ActivityMainBinding
import com.example.motos.utils.SessionManager
import com.example.motos.view.activity.LoginActivity
import com.example.motos.view.fragment.InicioFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    // TODO: SegundaManoFragment
                    true
                }
                R.id.nav_taller -> {
                    // TODO: TallerFragment
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
        val anchor = binding.bottomNav.findViewById<View>(R.id.nav_ajustes)

        val popup = PopupMenu(this, anchor)

        popup.menu.add(0, 1, 0, "Perfil")

        when (rol) {
            "CLIENTE" -> popup.menu.add(0, 2, 1, "Mis reservas")
            "MECANICO", "ADMIN" -> {
                popup.menu.add(0, 2, 1, "Citas")
                popup.menu.add(0, 3, 2, "Reparaciones")
            }
            "VENDEDOR" -> popup.menu.add(0, 2, 1, "Reservas")
        }

        popup.menu.add(0, 99, 99, "Cerrar sesión")

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> { /* TODO: PerfilFragment */ true }
                2 -> { /* TODO: según rol */ true }
                3 -> { /* TODO: según rol */ true }
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
    override fun onDestroy() { // hace que se desloguee al cerrar app, si se rota pantalla no cierra
        super.onDestroy()
        if (isFinishing) {
            session.logout()
        }
    }
}