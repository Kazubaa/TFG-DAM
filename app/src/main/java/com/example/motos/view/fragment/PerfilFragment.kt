package com.example.motos.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.motos.databinding.FragmentPerfilBinding
import com.example.motos.model.ClienteRequest
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.ClienteRepository
import com.example.motos.utils.SessionManager
import com.example.motos.viewmodel.PerfilViewModel
import com.example.motos.viewmodel.PerfilViewModelFactory

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager

    private val viewModel: PerfilViewModel by viewModels {
        PerfilViewModelFactory(
            ClienteRepository(RetrofitClient.getInstance(requireContext()))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SessionManager(requireContext())
        val rol = session.getRol() ?: "INVITADO"

        binding.tvUsername.text = session.getUsername() ?: "Usuario"
        binding.tvRol.text = rol

        binding.btnVolver.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Solo CLIENTE tiene perfil editable con datos de contacto
        if (rol != "CLIENTE") {
            binding.btnGuardar.visibility = View.GONE
            binding.etNombre.isEnabled = false
            binding.etEmail.isEnabled = false
            binding.etTelefono.isEnabled = false
            return
        }

        val clienteId = session.getClienteId()
        if (clienteId == -1L) {
            Toast.makeText(context, "No se encontró perfil del cliente", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.cliente.observe(viewLifecycleOwner) { cliente ->
            if (cliente != null) {
                binding.etNombre.setText(cliente.nombre)
                binding.etEmail.setText(cliente.email)
                binding.etTelefono.setText(cliente.telefono)
            }
        }

        viewModel.actualizacionCompleta.observe(viewLifecycleOwner) { ok ->
            binding.progressBar.visibility = View.GONE
            binding.btnGuardar.isEnabled = true
            if (ok) {
                Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.cargarPerfil(clienteId)

        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Guardar cambios")
                .setMessage("¿Quieres guardar estos datos en tu perfil?")
                .setPositiveButton("Guardar") { _, _ ->
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnGuardar.isEnabled = false
                    viewModel.actualizarPerfil(clienteId, ClienteRequest(nombre, email, telefono))
                }
                .setNegativeButton("Cancelar", null)
                .show()


            viewModel.actualizacionCompleta.observe(viewLifecycleOwner) { ok ->
                binding.progressBar.visibility = View.GONE
                binding.btnGuardar.isEnabled = true
                if (ok) {
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    viewModel.cargarPerfil(clienteId)  // recarga datos
                } else {
                    Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}