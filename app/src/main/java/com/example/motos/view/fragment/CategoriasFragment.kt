package com.example.motos.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.R
import com.example.motos.adapter.Categoria
import com.example.motos.adapter.CategoriaAdapter
import com.example.motos.databinding.FragmentCategoriasBinding
import kotlin.apply

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    private val categorias = listOf(
        Categoria("SUPERSPORT", "SUPERSPORT"),
        Categoria("NAKED", "NAKED"),
        Categoria("TOURING", "TOURING"),
        Categoria("SCOOTER", "SCOOTER"),
        Categoria("125CC", "125 CC")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val marca = arguments?.getString("marca") ?: ""
        binding.tvMarca.text = marca

        binding.btnVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        binding.rvCategorias.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CategoriaAdapter(categorias) { categoria ->
                val fragment = MotosNuevasListaFragment().apply {
                    arguments = Bundle().apply {
                        putString("marca", marca)
                        putString("categoria", categoria.key)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}