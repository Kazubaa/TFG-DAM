package com.example.motos.view.fragment

import android.R.attr.type
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.adapter.ImagenMotoNuevaAdapter
import com.example.motos.databinding.FragmentGestionarImagenesBinding
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.MotoNuevaRepository
import com.example.motos.viewmodel.MotoNuevaViewModel
import com.example.motos.viewmodel.MotoNuevaViewModelFactory
import java.io.File
import java.io.FileOutputStream

class GestionarImagenesMotoNuevaFragment : Fragment() {

    private var _binding: FragmentGestionarImagenesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterGaleria: ImagenMotoNuevaAdapter
    private lateinit var adapter360: ImagenMotoNuevaAdapter

    private var motoId: Long = -1L

    private var seleccionadasGaleria: List<Uri> = emptyList()
    private var seleccionadas360: List<Uri> = emptyList()
    private var videoSeleccionado: Uri? = null

    private val viewModel: MotoNuevaViewModel by viewModels {
        MotoNuevaViewModelFactory(MotoNuevaRepository(RetrofitClient.getInstance(requireContext())))
    }

    private val pickGaleria = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        seleccionadasGaleria = uris
        if (uris.isNotEmpty()) {
            binding.tvSeleccionadasGaleria.visibility = View.VISIBLE
            binding.tvSeleccionadasGaleria.text = "${uris.size} foto(s) seleccionada(s)"
            binding.btnSubirGaleria.visibility = View.VISIBLE
        } else {
            binding.tvSeleccionadasGaleria.visibility = View.GONE
            binding.btnSubirGaleria.visibility = View.GONE
        }
    }

    private val pick360 = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        seleccionadas360 = uris
        if (uris.isNotEmpty()) {
            binding.tvSeleccionadas360.visibility = View.VISIBLE
            binding.tvSeleccionadas360.text = "${uris.size} foto(s) seleccionada(s)"
            binding.btnSubir360.visibility = View.VISIBLE
        } else {
            binding.tvSeleccionadas360.visibility = View.GONE
            binding.btnSubir360.visibility = View.GONE
        }
    }

    private val pickVideo = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            videoSeleccionado = uri
            binding.tvVideoSeleccionado.visibility = View.VISIBLE
            binding.tvVideoSeleccionado.text = "Vídeo seleccionado"
            binding.btnSubirVideo.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionarImagenesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motoId = arguments?.getLong("motoId") ?: -1L

        binding.btnVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        adapterGaleria = ImagenMotoNuevaAdapter(emptyList()) { img ->
            viewModel.eliminarImagen(img.id, motoId, "GALERIA")
        }
        adapter360 = ImagenMotoNuevaAdapter(emptyList()) { img ->
            viewModel.eliminarImagen(img.id, motoId, "R360")
        }

        binding.rvGaleria.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterGaleria
        }
        binding.rv360.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapter360
        }

        viewModel.imagenesGaleria.observe(viewLifecycleOwner) { adapterGaleria.updateList(it) }
        viewModel.imagenes360.observe(viewLifecycleOwner) { adapter360.updateList(it) }

        viewModel.motoActual.observe(viewLifecycleOwner) { moto ->
            binding.btnEliminarVideo.visibility =
                if (moto?.videoFile != null) View.VISIBLE else View.GONE
        }

        binding.btnSeleccionarGaleria.setOnClickListener { pickGaleria.launch(arrayOf("image/*")) }
        binding.btnSeleccionar360.setOnClickListener { pick360.launch(arrayOf("image/*")) }
        binding.btnSeleccionarVideo.setOnClickListener { pickVideo.launch(arrayOf("video/*")) }

        binding.btnSubirGaleria.setOnClickListener {
            seleccionadasGaleria.forEach { uri ->
                val file = uriToFile(uri, "jpg") ?: return@forEach
                viewModel.subirImagen(motoId, file, "GALERIA")
            }
            seleccionadasGaleria = emptyList()
            binding.tvSeleccionadasGaleria.visibility = View.GONE
            binding.btnSubirGaleria.visibility = View.GONE
        }

        binding.btnSubir360.setOnClickListener {
            seleccionadas360.forEach { uri ->
                val file = uriToFile(uri, "jpg") ?: return@forEach
                viewModel.subirImagen(motoId, file, "R360")
            }
            seleccionadas360 = emptyList()
            binding.tvSeleccionadas360.visibility = View.GONE
            binding.btnSubir360.visibility = View.GONE
        }

        binding.btnSubirVideo.setOnClickListener {
            videoSeleccionado?.let { uri ->
                val file = uriToFile(uri, "mp4")
                if (file != null) {
                    viewModel.subirVideo(motoId, file)
                    videoSeleccionado = null
                    binding.tvVideoSeleccionado.visibility = View.GONE
                    binding.btnSubirVideo.visibility = View.GONE
                }
            }
        }

        binding.btnEliminarVideo.setOnClickListener {
            viewModel.eliminarVideo(motoId)
            binding.btnEliminarVideo.visibility = View.GONE
        }

        viewModel.cargarMoto(motoId)
    }

    private fun uriToFile(uri: Uri, ext: String): File? {
        return try {
            val input = requireContext().contentResolver.openInputStream(uri) ?: return null
            val file = File(requireContext().cacheDir, "file_${System.currentTimeMillis()}_${(0..999999).random()}.$ext")
            FileOutputStream(file).use { input.copyTo(it) }
            file
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}