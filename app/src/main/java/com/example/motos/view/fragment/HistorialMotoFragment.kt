package com.example.motos.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.adapter.HistorialAdapter
import com.example.motos.databinding.FragmentHistorialMotoBinding
import com.example.motos.model.Reparacion
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.TallerRepository
import com.example.motos.utils.SessionManager
import com.example.motos.viewmodel.TallerViewModel
import com.example.motos.viewmodel.TallerViewModelFactory

class HistorialMotoFragment : Fragment() {

    private var _binding: FragmentHistorialMotoBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager
    private lateinit var adapter: HistorialAdapter
    private var matricula: String = ""

    private val viewModel: TallerViewModel by viewModels {
        TallerViewModelFactory(TallerRepository(RetrofitClient.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistorialMotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SessionManager(requireContext())
        val rol = session.getRol() ?: "INVITADO"
        val clienteId = session.getClienteId()

        matricula = arguments?.getString("matricula") ?: ""
        val marca = arguments?.getString("marca") ?: ""
        val modelo = arguments?.getString("modelo") ?: ""
        val km = arguments?.getInt("km") ?: 0

        binding.tvMarcaModelo.text = "$marca $modelo"
        binding.tvMatricula.text = matricula
        binding.tvKm.text = "$km km"

        binding.btnVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        adapter = HistorialAdapter(emptyList()) { rep -> abrirDetalle(rep) }
        binding.rvHistorial.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HistorialMotoFragment.adapter
        }

        viewModel.reparaciones.observe(viewLifecycleOwner) { reps ->
            val historial = reps.filter {
                it.estado == "COMPLETADO" && it.cita.motoCliente?.matricula == matricula
            }
            adapter.updateList(historial)
            binding.tvEmpty.visibility = if (historial.isEmpty()) View.VISIBLE else View.GONE
        }

        cargar(rol, clienteId)
    }

    private fun cargar(rol: String, clienteId: Long) {
        when (rol) {
            "CLIENTE" -> if (clienteId != -1L) viewModel.cargarReparacionesCliente(clienteId)
            "MECANICO", "ADMIN" -> viewModel.cargarReparacionesMoto(matricula)
        }
    }

    private fun abrirDetalle(rep: Reparacion) {
        val fragment = CitaDetalleFragment().apply {
            arguments = Bundle().apply {
                putLong("citaId", rep.cita.id)
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