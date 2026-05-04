package com.example.motos.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.motos.R
import com.example.motos.databinding.FragmentAnadirMotoNuevaBinding
import com.example.motos.model.MotoNuevaRequest
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.MotoNuevaRepository
import com.example.motos.viewmodel.MotoNuevaViewModel
import com.example.motos.viewmodel.MotoNuevaViewModelFactory

class AnadirMotoNuevaFragment : Fragment() {

    private var _binding: FragmentAnadirMotoNuevaBinding? = null
    private val binding get() = _binding!!

    private var motoId: Long = -1L
    private var modoEdicion = false

    private val viewModel: MotoNuevaViewModel by viewModels {
        MotoNuevaViewModelFactory(MotoNuevaRepository(RetrofitClient.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnadirMotoNuevaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motoId = arguments?.getLong("motoId", -1L) ?: -1L
        modoEdicion = motoId != -1L

        val marca = arguments?.getString("marca") ?: ""
        val categoria = arguments?.getString("categoria") ?: ""

        binding.etMarca.setText(marca)
        binding.etCategoria.setText(categoria)

        binding.btnVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        if (modoEdicion) {
            binding.btnGuardar.text = "GUARDAR CAMBIOS"
            viewModel.cargarMoto(motoId)
        }

        viewModel.motoActual.observe(viewLifecycleOwner) { moto ->
            if (moto == null) return@observe

            if (modoEdicion) {
                binding.etMarca.setText(moto.marca)
                binding.etCategoria.setText(moto.categoria)
                binding.etModelo.setText(moto.modelo)
                binding.etAnio.setText(moto.anio.toString())
                binding.etPrecio.setText(moto.precio.toString())
                binding.etCilindrada.setText(moto.cilindrada.toString())
                binding.etCv.setText(moto.cv.toString())
                binding.etPeso.setText(moto.peso.toString())
                binding.etDescripcion.setText(moto.descripcion)
            } else {
                Toast.makeText(context, "Moto creada", Toast.LENGTH_SHORT).show()
                val fragment = GestionarImagenesMotoNuevaFragment().apply {
                    arguments = Bundle().apply { putLong("motoId", moto.id) }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit()
            }
        }

        viewModel.accionCompleta.observe(viewLifecycleOwner) { ok ->
            if (ok == true && modoEdicion) {
                Toast.makeText(context, "Cambios guardados", Toast.LENGTH_SHORT).show()
                viewModel.resetAccionCompleta()
                parentFragmentManager.popBackStack()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                viewModel.resetError()
            }
        }

        binding.btnGuardar.setOnClickListener {
            val modelo = binding.etModelo.text.toString().trim()
            val anio = binding.etAnio.text.toString().toIntOrNull()
            val precio = binding.etPrecio.text.toString().toDoubleOrNull()
            val cilindrada = binding.etCilindrada.text.toString().toIntOrNull()
            val cv = binding.etCv.text.toString().toIntOrNull()
            val peso = binding.etPeso.text.toString().toIntOrNull()
            val descripcion = binding.etDescripcion.text.toString().trim()
            val marcaFinal = binding.etMarca.text.toString().trim()
            val categoriaFinal = binding.etCategoria.text.toString().trim()

            if (modelo.isEmpty() || anio == null || precio == null ||
                cilindrada == null || cv == null || peso == null) {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = MotoNuevaRequest(
                marca = marcaFinal,
                modelo = modelo,
                categoria = categoriaFinal,
                anio = anio,
                precio = precio,
                cilindrada = cilindrada,
                cv = cv,
                peso = peso,
                descripcion = descripcion
            )

            if (modoEdicion) {
                viewModel.actualizar(motoId, request)
            } else {
                viewModel.crear(request)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}