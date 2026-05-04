package com.example.motos.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.adapter.ReservaAdapter
import com.example.motos.databinding.FragmentReservasBinding
import com.example.motos.model.Reserva
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.MotoSegundaManoRepository
import com.example.motos.utils.SessionManager
import com.example.motos.viewmodel.ReservaViewModel
import com.example.motos.viewmodel.ReservaViewModelFactory

class ReservasFragment : Fragment() {

    private var _binding: FragmentReservasBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReservaAdapter
    private lateinit var session: SessionManager

    private val viewModel: ReservaViewModel by viewModels {
        ReservaViewModelFactory(
            MotoSegundaManoRepository(RetrofitClient.getInstance(requireContext()))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SessionManager(requireContext())
        val rol = session.getRol() ?: "INVITADO"
        val esAdmin = rol == "ADMIN" || rol == "VENDEDOR"

        binding.tvTitulo.text = if (esAdmin) "SOLICITUDES DE RESERVAS" else "MIS RESERVAS"
        binding.btnVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        adapter = ReservaAdapter(
            items = emptyList(),
            mostrarAcciones = esAdmin,
            onItemClick = { reserva -> abrirMoto(reserva) },
            onAceptar = { reserva -> viewModel.actualizarEstado(reserva.id, "ACEPTADA") },
            onRechazar = { reserva -> viewModel.actualizarEstado(reserva.id, "RECHAZADA") }
        )
        binding.rvReservas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ReservasFragment.adapter
        }

        viewModel.reservas.observe(viewLifecycleOwner) { list ->
            val ordenadas = list.sortedByDescending { it.id }
            adapter.updateList(ordenadas)
            binding.tvEmpty.visibility = if (ordenadas.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.accionCompletada.observe(viewLifecycleOwner) {
            cargarReservas(esAdmin)
        }

        cargarReservas(esAdmin)
    }

    private fun cargarReservas(esAdmin: Boolean) {
        if (esAdmin) viewModel.cargarTodasReservas()
        else viewModel.cargarReservasCliente(session.getClienteId())
    }

    private fun abrirMoto(reserva: Reserva) {
        val moto = reserva.motoSegundaMano ?: return
        val fragment = MotoSegundaManoDetalleFragment().apply {
            arguments = Bundle().apply {
                putLong("motoId", moto.id)
                putString("marca", moto.marca)
                putString("modelo", moto.modelo)
                putDouble("precio", moto.precio)
                putInt("cilindrada", moto.cilindrada)
                putInt("cv", moto.cv)
                putInt("km", moto.km)
                putString("matricula", moto.matricula)
                putBoolean("disponible", moto.disponible)
                putString("imagenPrincipal", moto.imagenPrincipal)
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