package com.example.motos.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.adapter.MotoClienteAdapter
import com.example.motos.databinding.DialogAnadirMotoClienteBinding
import com.example.motos.databinding.FragmentPerfilBinding
import com.example.motos.model.ClienteRequest
import com.example.motos.model.MecanicoRequest
import com.example.motos.model.MotoCliente
import com.example.motos.model.MotoClienteRequest
import com.example.motos.model.VendedorRequest
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.ClienteRepository
import com.example.motos.repository.PerfilRepository
import com.example.motos.repository.TallerRepository
import com.example.motos.utils.SessionManager
import com.example.motos.viewmodel.PerfilViewModel
import com.example.motos.viewmodel.PerfilViewModelFactory
import com.example.motos.viewmodel.TallerViewModel
import com.example.motos.viewmodel.TallerViewModelFactory

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager
    private lateinit var motoAdapter: MotoClienteAdapter

    private val perfilViewModel: PerfilViewModel by viewModels {
        PerfilViewModelFactory(
            ClienteRepository(RetrofitClient.getInstance(requireContext())),
            PerfilRepository(RetrofitClient.getInstance(requireContext()))
        )
    }

    private val tallerViewModel: TallerViewModel by viewModels {
        TallerViewModelFactory(TallerRepository(RetrofitClient.getInstance(requireContext())))
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

        binding.btnVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        binding.headerDatos.setOnClickListener {
            if (binding.layoutDatos.visibility == View.VISIBLE) {
                binding.layoutDatos.visibility = View.GONE
                binding.ivArrowDatos.setImageResource(android.R.drawable.arrow_up_float)
            } else {
                binding.layoutDatos.visibility = View.VISIBLE
                binding.ivArrowDatos.setImageResource(android.R.drawable.arrow_down_float)
            }
        }

        perfilViewModel.actualizacionCompleta.observe(viewLifecycleOwner) { ok ->
            if (ok) Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
        }

        when (rol) {
            "CLIENTE" -> setupCliente()
            "MECANICO" -> setupMecanico()
            "VENDEDOR" -> setupVendedor()
            else -> {
                binding.btnGuardar.visibility = View.GONE
                binding.etNombre.isEnabled = false
                binding.etEmail.isEnabled = false
                binding.etTelefono.isEnabled = false
                binding.layoutVehiculos.visibility = View.GONE
            }
        }
    }

    private fun setupCliente() {
        val clienteId = session.getClienteId()
        if (clienteId == -1L) {
            binding.btnGuardar.visibility = View.GONE
            binding.layoutVehiculos.visibility = View.GONE
            return
        }

        setupRecyclerMotos()

        perfilViewModel.cliente.observe(viewLifecycleOwner) { cliente ->
            if (cliente != null) {
                binding.etNombre.setText(cliente.nombre)
                binding.etEmail.setText(cliente.email)
                binding.etTelefono.setText(cliente.telefono)
            }
        }

        tallerViewModel.motos.observe(viewLifecycleOwner) { list ->
            motoAdapter.updateList(list)
            binding.tvSinVehiculos.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        tallerViewModel.accionCompleta.observe(viewLifecycleOwner) {
            tallerViewModel.cargarMotosCliente(clienteId)
        }

        tallerViewModel.error.observe(viewLifecycleOwner) { msg ->
            if (msg != null) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Guardar cambios")
                .setMessage("¿Guardar estos datos?")
                .setPositiveButton("Guardar") { _, _ ->
                    perfilViewModel.actualizarCliente(clienteId, ClienteRequest(nombre, email, telefono))
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.btnAnadirMoto.setOnClickListener { mostrarDialogoAnadirMoto(clienteId) }

        perfilViewModel.cargarCliente(clienteId)
        tallerViewModel.cargarMotosCliente(clienteId)
    }

    private fun setupMecanico() {
        val mecanicoId = session.getMecanicoId()
        binding.layoutVehiculos.visibility = View.GONE

        if (mecanicoId == -1L) {
            Toast.makeText(context, "No se ha encontrado tu mecánico", Toast.LENGTH_SHORT).show()
            binding.btnGuardar.visibility = View.GONE
            return
        }

        perfilViewModel.mecanico.observe(viewLifecycleOwner) { m ->
            if (m != null) {
                binding.etNombre.setText(m.nombre)
                binding.etEmail.setText(m.email)
                binding.etTelefono.setText(m.telefono)
            }
        }

        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Guardar cambios")
                .setMessage("¿Guardar estos datos?")
                .setPositiveButton("Guardar") { _, _ ->
                    perfilViewModel.actualizarMecanico(mecanicoId,
                        MecanicoRequest(nombre, email, telefono)
                    )
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        perfilViewModel.cargarMecanico(mecanicoId)
    }

    private fun setupVendedor() {
        val vendedorId = session.getVendedorId()
        binding.layoutVehiculos.visibility = View.GONE

        if (vendedorId == -1L) {
            Toast.makeText(context, "No se ha encontrado tu vendedor", Toast.LENGTH_SHORT).show()
            binding.btnGuardar.visibility = View.GONE
            return
        }

        perfilViewModel.vendedor.observe(viewLifecycleOwner) { v ->
            if (v != null) {
                binding.etNombre.setText(v.nombre)
                binding.etEmail.setText(v.email)
                binding.etTelefono.setText(v.telefono)
            }
        }

        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Guardar cambios")
                .setMessage("¿Guardar estos datos?")
                .setPositiveButton("Guardar") { _, _ ->
                    perfilViewModel.actualizarVendedor(vendedorId,
                        VendedorRequest(nombre, email, telefono)
                    )
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        perfilViewModel.cargarVendedor(vendedorId)
    }

    private fun setupRecyclerMotos() {
        motoAdapter = MotoClienteAdapter(
            items = emptyList(),
            onClick = { moto -> abrirHistorial(moto) },
            onEliminar = { moto -> confirmarEliminar(moto) }
        )
        binding.rvVehiculos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = motoAdapter
        }
    }

    private fun mostrarDialogoAnadirMoto(clienteId: Long) {
        val dialogBinding = DialogAnadirMotoClienteBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Añadir") { _, _ ->
                val matricula = dialogBinding.etMatricula.text.toString().trim().uppercase()
                val marca = dialogBinding.etMarca.text.toString().trim()
                val modelo = dialogBinding.etModelo.text.toString().trim()
                val km = dialogBinding.etKm.text.toString().toIntOrNull()

                if (matricula.isEmpty() || marca.isEmpty() || modelo.isEmpty() || km == null) {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                tallerViewModel.crearMotoCliente(
                    MotoClienteRequest(matricula, marca, modelo, km, clienteId)
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarEliminar(moto: MotoCliente) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar vehículo")
            .setMessage("¿Eliminar ${moto.marca} ${moto.modelo} (${moto.matricula})?")
            .setPositiveButton("Eliminar") { _, _ ->
                tallerViewModel.eliminarMotoCliente(moto.matricula)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirHistorial(moto: MotoCliente) {
        val fragment = HistorialMotoFragment().apply {
            arguments = Bundle().apply {
                putString("matricula", moto.matricula)
                putString("marca", moto.marca)
                putString("modelo", moto.modelo)
                putInt("km", moto.km)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(com.example.motos.R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}