package com.example.motos.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.R
import com.example.motos.adapter.MotoNuevaAdapter
import com.example.motos.databinding.FragmentMotosNuevasListaBinding
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.MotoNuevaRepository
import com.example.motos.utils.SessionManager
import com.example.motos.viewmodel.MotoNuevaViewModel
import com.example.motos.viewmodel.MotoNuevaViewModelFactory
import kotlinx.coroutines.launch

class MotosNuevasListaFragment : Fragment() {

    private var _binding: FragmentMotosNuevasListaBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MotoNuevaAdapter
    private lateinit var session: SessionManager
    private var marca: String = ""
    private var categoria: String = ""

    private val viewModel: MotoNuevaViewModel by viewModels {
        MotoNuevaViewModelFactory(MotoNuevaRepository(RetrofitClient.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMotosNuevasListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SessionManager(requireContext())
        val rol = session.getRol() ?: "INVITADO"

        marca = arguments?.getString("marca") ?: ""
        categoria = arguments?.getString("categoria") ?: ""

        binding.tvTitulo.text = "$marca · $categoria"

        binding.btnVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        if (rol == "ADMIN" || rol == "VENDEDOR") {
            binding.btnAdmin.visibility = View.VISIBLE
            binding.btnAdmin.setOnClickListener {
                val fragment = AnadirMotoNuevaFragment().apply {
                    arguments = Bundle().apply {
                        putString("marca", marca)
                        putString("categoria", categoria)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        adapter = MotoNuevaAdapter(emptyList(), emptyMap()) { moto ->
            val fragment = MotoNuevaDetalleFragment().apply {
                arguments = Bundle().apply { putLong("motoId", moto.id) }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvMotos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MotosNuevasListaFragment.adapter
        }

        viewModel.motos.observe(viewLifecycleOwner) { list ->
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            // Carga la primera imagen de galería de cada moto
            viewLifecycleOwner.lifecycleScope.launch {
                val portadas = mutableMapOf<Long, String>()
                val repo = MotoNuevaRepository(RetrofitClient.getInstance(requireContext()))
                list.forEach { m ->
                    val imgs = repo.getImagenesPorTipo(m.id, "GALERIA")
                    if (imgs.isNotEmpty()) portadas[m.id] = imgs.first().url
                }
                adapter.updateList(list, portadas)
            }
        }

        viewModel.cargarPorMarcaCategoria(marca, categoria)
    }

    override fun onResume() {
        super.onResume()
        if (marca.isNotEmpty() && categoria.isNotEmpty()) {
            viewModel.cargarPorMarcaCategoria(marca, categoria)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}