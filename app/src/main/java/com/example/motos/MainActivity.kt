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
import com.example.motos.view.fragment.MotoSegundaManoFragment
import com.example.motos.view.fragment.PerfilFragment
import com.example.motos.view.fragment.ReservasFragment
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
        android.util.Log.d("ROL_DETALLE", "Rol recibido: '$rol'")
        val anchor = binding.bottomNav.findViewById<View>(R.id.nav_ajustes)

        val popup = PopupMenu(this, anchor)

        popup.menu.add(0, 1, 0, "Perfil")

        if (rol == "CLIENTE" || rol == "ADMIN") {
            popup.menu.add(0, 2, 1, "Mis reservas")
        }
        if (rol == "MECANICO" || rol == "ADMIN") {
            popup.menu.add(0, 3, 2, "Citas")
            popup.menu.add(0, 4, 3, "Reparaciones")
        }
        if (rol == "ADMIN" || rol == "VENDEDOR") {
            popup.menu.add(0, 5, 4, "Solicitudes de reservas")
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
                3 -> { /* TODO: Mecanico */ true }
                4 ->{ /* TODO: Funcion mecanico mostrar citas*/true}
                5 -> {
                    // Solicitudes de reservas
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ReservasFragment())
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
    override fun onDestroy() { // hace que se desloguee al cerrar app, si se rota pantalla no cierra
        super.onDestroy()
        if (isFinishing) {
            session.logout()
        }
    }
}