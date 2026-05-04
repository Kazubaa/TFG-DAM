package com.example.motos.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.motos.R
import com.example.motos.adapter.Marca
import com.example.motos.adapter.MarcaAdapter
import com.example.motos.databinding.FragmentMarcasBinding

class MarcasFragment : Fragment() {

    private var _binding: FragmentMarcasBinding? = null
    private val binding get() = _binding!!

    private val marcas = listOf(
        Marca("HONDA", R.drawable.logo_honda),
        Marca("YAMAHA", R.drawable.logo_yamaha),
        Marca("KTM", R.drawable.logo_ktm),
        Marca("KAWASAKI", R.drawable.logo_kawasaki),
        Marca("SUZUKI", R.drawable.logo_suzuki)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarcasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMarcas.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = MarcaAdapter(marcas) { marca ->
                val fragment = CategoriasFragment().apply {
                    arguments = Bundle().apply {
                        putString("marca", marca.nombre)
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