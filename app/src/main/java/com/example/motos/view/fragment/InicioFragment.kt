package com.example.motos.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.adapter.PromocionAdapter
import com.example.motos.databinding.FragmentInicioBinding
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.PromocionRepository
import com.example.motos.utils.SessionManager
import com.example.motos.viewmodel.PromocionViewModel
import com.example.motos.viewmodel.PromocionViewModelFactory

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterMotos: PromocionAdapter
    private lateinit var adapterMecanico: PromocionAdapter

    private val viewModel: PromocionViewModel by viewModels {
        PromocionViewModelFactory(
            PromocionRepository(RetrofitClient.getInstance(requireContext()))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString("username")
            ?: SessionManager(requireContext()).getUsername()
            ?: "Invitado"

        binding.tvWelcome.text = "Bienvenido, $username"

        setupRecyclers()
        observeViewModel()
        viewModel.cargarPromociones()
    }

    private fun setupRecyclers() {
        adapterMotos = PromocionAdapter(emptyList())
        binding.rvMotos.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterMotos
        }

        adapterMecanico = PromocionAdapter(emptyList())
        binding.rvMecanico.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterMecanico
        }
    }

    private fun observeViewModel() {
        viewModel.promocionesMotos.observe(viewLifecycleOwner) {
            adapterMotos.updateList(it)
        }
        viewModel.promocionesMecanico.observe(viewLifecycleOwner) {
            adapterMecanico.updateList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}