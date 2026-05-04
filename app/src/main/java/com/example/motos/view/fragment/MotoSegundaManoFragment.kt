package com.example.motos.view.fragment

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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

    private var todasLasMotos: List<MotoSegundaMano> = emptyList()

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

        val rol = com.example.motos.utils.SessionManager(requireContext()).getRol() ?: "INVITADO"
        viewModel.cargarMotos(rol)
    }

    private fun setupRecycler() {
        adapter = MotoSegundaManoAdapter(emptyList()) { moto -> abrirDetalle(moto) }
        binding.rvMotos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MotoSegundaManoFragment.adapter
        }
    }

    private fun setupFiltros() {
        binding.btnFiltro.setOnClickListener {
            val visible = binding.layoutFiltros.visibility == View.VISIBLE
            binding.layoutFiltros.visibility = if (visible) View.GONE else View.VISIBLE
        }

        binding.btnAplicar.setOnClickListener {
            aplicarFiltro()
            binding.layoutFiltros.visibility = View.GONE
        }

        binding.btnLimpiar.setOnClickListener {
            binding.etFiltroMarca.text?.clear()
            binding.etFiltroModelo.text?.clear()
            binding.etFiltroPrecio.text?.clear()
            binding.etFiltroCv.text?.clear()
            binding.etFiltroKm.text?.clear()
            binding.etFiltroCilindrada.text?.clear()
            binding.layoutFiltros.visibility = View.GONE
            adapter.updateList(todasLasMotos)
        }
    }

    private fun aplicarFiltro() {
        val marcaQ = binding.etFiltroMarca.text.toString().trim().lowercase()
        val modeloQ = binding.etFiltroModelo.text.toString().trim().lowercase()
        val precioMax = binding.etFiltroPrecio.text.toString().toDoubleOrNull()
        val cvMin = binding.etFiltroCv.text.toString().toIntOrNull()
        val kmMax = binding.etFiltroKm.text.toString().toIntOrNull()
        val cilindradaMin = binding.etFiltroCilindrada.text.toString().toIntOrNull()

        val filtradas = todasLasMotos.filter { moto ->
            val matchMarca = marcaQ.isBlank() || coincideTolerante(moto.marca, marcaQ)
            val matchModelo = modeloQ.isBlank() || coincideTolerante(moto.modelo, modeloQ)
            val matchPrecio = precioMax == null || moto.precio <= precioMax
            val matchCv = cvMin == null || moto.cv >= cvMin
            val matchKm = kmMax == null || moto.km <= kmMax
            val matchCilindrada = cilindradaMin == null || moto.cilindrada >= cilindradaMin

            matchMarca && matchModelo && matchPrecio && matchCv && matchKm && matchCilindrada
        }
        adapter.updateList(filtradas)
    }

    //Comprueba si una marca/modelo coincide con la búsqueda del usuario, tolerando errores tipográficos.
    // texto valor real de la BD (ej. "Kawasaki") query lo escrito por el usuario en minúsculas (ej. "kasawaki") @return true si se considera coincidencia
    private fun coincideTolerante(texto: String, query: String): Boolean {
        val txt = texto.lowercase()
        if (txt.contains(query)) return true
        // Comprueba palabras del texto contra el query
        return txt.split(" ").any { palabra ->
            distanciaLevenshtein(palabra, query) <= 2 ||
                    palabra.startsWith(query) ||
                    query.startsWith(palabra.take(3))
        } || distanciaLevenshtein(txt, query) <= 2
    }

    private fun distanciaLevenshtein(a: String, b: String): Int {
        val m = a.length
        val n = b.length
        if (m == 0) return n
        if (n == 0) return m
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[m][n]
    }

    private fun observeViewModel() {
        viewModel.motoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MotoState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is MotoState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    todasLasMotos = state.data
                    adapter.updateList(todasLasMotos)
                    configurarAutocompletado()
                }
                is MotoState.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun configurarAutocompletado() {
        val marcas = todasLasMotos.map { it.marca }.distinct()
        val modelos = todasLasMotos.map { it.modelo }.distinct()

        // Solo si los campos son AutoCompleteTextView
        (binding.etFiltroMarca as? android.widget.AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, marcas)
        )
        (binding.etFiltroModelo as? android.widget.AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modelos)
        )
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