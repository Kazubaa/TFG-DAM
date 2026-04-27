package com.example.motos.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.adapter.MotoClienteAdapter
import com.example.motos.databinding.FragmentListaMotosBinding
import com.example.motos.model.MotoCliente
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.TallerRepository
import com.example.motos.viewmodel.TallerViewModel
import com.example.motos.viewmodel.TallerViewModelFactory

class HistorialClientesFragment : Fragment() {

    private var _binding: FragmentListaMotosBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MotoClienteAdapter

    private val viewModel: TallerViewModel by viewModels {
        TallerViewModelFactory(TallerRepository(RetrofitClient.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaMotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        adapter = MotoClienteAdapter(
            items = emptyList(),
            onClick = { moto -> abrirHistorial(moto) },
            onEliminar = { },
            mostrarEliminar = false
        )
        binding.rvMotos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HistorialClientesFragment.adapter
        }

        viewModel.reparaciones.observe(viewLifecycleOwner) { reps ->
            val motosConHistorial = reps
                .filter { it.estado == "COMPLETADO" }
                .mapNotNull { it.cita.motoCliente }
                .distinctBy { it.matricula }

            adapter.updateList(motosConHistorial)
            binding.tvEmpty.visibility = if (motosConHistorial.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.cargarReparaciones()
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