package com.example.motos.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.adapter.ImagenLocalAdapter
import com.example.motos.databinding.FragmentAnadirMotoSegundaManoBinding
import com.example.motos.model.MotoSegundaManoRequest
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.MotoSegundaManoRepository
import com.example.motos.viewmodel.MotoSegundaManoViewModel
import com.example.motos.viewmodel.MotoSegundaManoViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AnadirMotoSegundaManoFragment : Fragment() {

    private var _binding: FragmentAnadirMotoSegundaManoBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ImagenLocalAdapter
    private val imagenesSeleccionadas = mutableListOf<Uri>()

    private val viewModel: MotoSegundaManoViewModel by viewModels {
        MotoSegundaManoViewModelFactory(
            MotoSegundaManoRepository(RetrofitClient.getInstance(requireContext()))
        )
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data?.clipData != null) {
                for (i in 0 until data.clipData!!.itemCount) {
                    imagenesSeleccionadas.add(data.clipData!!.getItemAt(i).uri)
                }
            } else if (data?.data != null) {
                imagenesSeleccionadas.add(data.data!!)
            }
            adapter.updateList(imagenesSeleccionadas)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnadirMotoSegundaManoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ImagenLocalAdapter(imagenesSeleccionadas) { uri ->
            imagenesSeleccionadas.remove(uri)
            adapter.updateList(imagenesSeleccionadas)
        }
        binding.rvImagenes.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = this@AnadirMotoSegundaManoFragment.adapter
        }

        binding.btnVolver.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSeleccionarImagenes.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                type = "image/*"
            }
            imagePickerLauncher.launch(intent)
        }

        binding.btnCrear.setOnClickListener { crearMoto() }

    }

    private fun crearMoto() {
        val marca = binding.etMarca.text.toString().trim()
        val modelo = binding.etModelo.text.toString().trim()
        val precio = binding.etPrecio.text.toString().toDoubleOrNull()
        val cilindrada = binding.etCilindrada.text.toString().toIntOrNull()
        val km = binding.etKm.text.toString().toIntOrNull()
        val cv = binding.etCv.text.toString().toIntOrNull()
        val matricula = binding.etMatricula.text.toString().trim()

        if (marca.isEmpty() || modelo.isEmpty() || precio == null ||
            cilindrada == null ||km == null || cv == null || matricula.isEmpty()) {
            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnCrear.isEnabled = false

        val request = MotoSegundaManoRequest(marca, modelo, precio, cilindrada, km, cv, matricula)
        viewModel.crearMoto(request, imagenesSeleccionadas) { motoCreada ->
            // Subir imágenes después de crear la moto
            if (motoCreada != null && imagenesSeleccionadas.isNotEmpty()) {
                subirImagenes(motoCreada.id)
            } else {
                finalizar()
            }
        }
    }

    private fun subirImagenes(motoId: Long) {
        val context = requireContext()
        var contador = 0
        val total = imagenesSeleccionadas.size

        imagenesSeleccionadas.forEach { uri ->
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@forEach
            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { inputStream.copyTo(it) }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val motoIdBody = motoId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.subirImagenSilencio(motoIdBody, body) {
                contador++
                if (contador >= total) finalizar()
            }
        }
    }

    private fun finalizar() {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(context, "Moto creada correctamente", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}