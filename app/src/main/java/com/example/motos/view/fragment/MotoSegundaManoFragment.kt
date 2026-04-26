package com.example.motos.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.adapter.MotoSegundaManoAdapter
import com.example.motos.databinding.FragmentMotoSegundaManoBinding
import com.example.motos.model.MotoSegundaMano
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.MotoSegundaManoRepository
import com.example.motos.viewmodel.MotoSegundaManoViewModel
import com.example.motos.viewmodel.MotoSegundaManoViewModelFactory
import com.example.motos.viewmodel.MotoState

class MotoSegundaManoFragment : Fragment() {

    private var _binding: FragmentMotoSegundaManoBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MotoSegundaManoAdapter

    private val viewModel: MotoSegundaManoViewModel by viewModels {
        MotoSegundaManoViewModelFactory(
            MotoSegundaManoRepository(RetrofitClient.getInstance(requireContext()))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMotoSegundaManoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupFiltros()
        setupBtnAnadir()
        observeViewModel()
        viewModel.cargarMotos()

        val rol = com.example.motos.utils.SessionManager(requireContext()).getRol() ?: "INVITADO"
        viewModel.cargarMotos(rol)
    }

    private fun setupRecycler() {
        adapter = MotoSegundaManoAdapter(emptyList()) { moto ->
            abrirDetalle(moto)
        }
        binding.rvMotos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MotoSegundaManoFragment.adapter
        }
    }

    private fun setupFiltros() {
        val rol = com.example.motos.utils.SessionManager(requireContext()).getRol() ?: "INVITADO"

        binding.btnFiltro.setOnClickListener {
            val visible = binding.layoutFiltros.visibility == View.VISIBLE
            binding.layoutFiltros.visibility = if (visible) View.GONE else View.VISIBLE
        }

        binding.btnAplicar.setOnClickListener {
            val marca = binding.etFiltroMarca.text.toString().takeIf { it.isNotBlank() }
            val modelo = binding.etFiltroModelo.text.toString().takeIf { it.isNotBlank() }
            val precio = binding.etFiltroPrecio.text.toString().toDoubleOrNull()
            val cv = binding.etFiltroCv.text.toString().toIntOrNull()
            val km = binding.etFiltroKm.text.toString().toIntOrNull()
            val cilindrada = binding.etFiltroCilindrada.text.toString().toIntOrNull()
            binding.layoutFiltros.visibility = View.GONE
            viewModel.filtrar(rol, marca, cv, km,cilindrada, precio,)
        }

        binding.btnLimpiar.setOnClickListener {
            binding.etFiltroMarca.text?.clear()
            binding.etFiltroModelo.text?.clear()
            binding.etFiltroPrecio.text?.clear()
            binding.etFiltroCv.text?.clear()
            binding.etFiltroKm.text?.clear()
            binding.etFiltroCilindrada.text?.clear()
            binding.layoutFiltros.visibility = View.GONE
            viewModel.cargarMotos(rol)
        }
    }

    private fun observeViewModel() {
        viewModel.motoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MotoState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is MotoState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.updateList(state.data)
                }
                is MotoState.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun abrirDetalle(moto: MotoSegundaMano) {
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

    private fun setupBtnAnadir() {
        val rol = com.example.motos.utils.SessionManager(requireContext()).getRol() ?: "INVITADO"
        if (rol == "ADMIN" || rol == "VENDEDOR") {
            binding.btnAnadir.visibility = View.VISIBLE
            binding.btnAnadir.setOnClickListener {
                val fragment = AnadirMotoSegundaManoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(com.example.motos.R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val rol = com.example.motos.utils.SessionManager(requireContext()).getRol() ?: "INVITADO"
        viewModel.cargarMotos(rol)
    }
}