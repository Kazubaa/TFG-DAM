package com.example.motos.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.motos.R
import com.example.motos.adapter.GaleriaAdapter
import com.example.motos.databinding.FragmentMotoNuevaDetalleBinding
import com.example.motos.model.ImagenMotoNueva
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.MotoNuevaRepository
import com.example.motos.utils.SessionManager
import com.example.motos.viewmodel.MotoNuevaViewModel
import com.example.motos.viewmodel.MotoNuevaViewModelFactory

class MotoNuevaDetalleFragment : Fragment() {

    private var _binding: FragmentMotoNuevaDetalleBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager
    private var motoId: Long = -1L

    private var modo360 = false
    private var galeria: List<ImagenMotoNueva> = emptyList()
    private var imagenes360: List<ImagenMotoNueva> = emptyList()
    private var frame360 = 0

    private var player: androidx.media3.exoplayer.ExoPlayer? = null

    private var enFullscreen = false
    private var originalSystemUi = 0

    private val viewModel: MotoNuevaViewModel by viewModels {
        MotoNuevaViewModelFactory(MotoNuevaRepository(RetrofitClient.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMotoNuevaDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SessionManager(requireContext())
        val rol = session.getRol() ?: "INVITADO"

        motoId = arguments?.getLong("motoId") ?: -1L

        binding.btnVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        viewModel.motoActual.observe(viewLifecycleOwner) { moto ->
            if (moto != null) {
                binding.tvTitulo.text = "${moto.marca} ${moto.modelo}"
                binding.tvModelo.text = "${moto.marca} ${moto.modelo}"
                binding.tvAnio.text = "Año ${moto.anio}"
                binding.tvPrecio.text = "%.0f €".format(moto.precio)
                binding.tvSpecs.text = "Cilindrada: ${moto.cilindrada} cc\n" +
                        "Potencia: ${moto.cv} CV\n" +
                        "Peso: ${moto.peso} kg\n" +
                        "Categoría: ${moto.categoria}"
                binding.tvDescripcion.text = moto.descripcion.ifBlank { "(sin descripción)" }

                if (!moto.videoFile.isNullOrBlank()) {
                    configurarVideo(moto.videoFile)
                }
            }
        }

        viewModel.imagenesGaleria.observe(viewLifecycleOwner) { list ->
            galeria = list
            val baseUrl = com.example.motos.utils.Constants.BASE_URL.removeSuffix("/")
            val urls = list.map { "$baseUrl/uploads/imagenes/${it.url}" }
            binding.viewPagerGaleria.adapter = GaleriaAdapter(urls)
            list.forEach { img ->
                Glide.with(this).load("$baseUrl/uploads/imagenes/${img.url}").preload()
            }
        }

        viewModel.imagenes360.observe(viewLifecycleOwner) { list ->
            imagenes360 = list
            binding.btnToggle360.visibility = if (list.size >= 8) View.VISIBLE else View.GONE

            val baseUrl = com.example.motos.utils.Constants.BASE_URL.removeSuffix("/")
            list.forEach { img ->
                Glide.with(this).load("$baseUrl/uploads/imagenes/${img.url}").preload()
            }
        }

        binding.btnToggle360.setOnClickListener { toggle360() }

        configurarTouch360()

        if (rol == "ADMIN" || rol == "VENDEDOR") {
            binding.btnEliminar.visibility = View.VISIBLE
            binding.btnGestionarImagenes.visibility = View.VISIBLE

            binding.btnEliminar.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar moto")
                    .setMessage("¿Eliminar esta moto y todas sus imágenes?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        viewModel.eliminar(motoId)
                        parentFragmentManager.popBackStack()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            binding.btnEditar.visibility = View.VISIBLE
            binding.btnEditar.setOnClickListener {
                val fragment = AnadirMotoNuevaFragment().apply {
                    arguments = Bundle().apply { putLong("motoId", motoId) }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            binding.btnGestionarImagenes.setOnClickListener {
                val fragment = GestionarImagenesMotoNuevaFragment().apply {
                    arguments = Bundle().apply { putLong("motoId", motoId) }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        viewModel.cargarMoto(motoId)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (enFullscreen) {
                        toggleFullscreen(false)
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    private fun configurarVideo(videoFile: String) {
        val baseUrl = com.example.motos.utils.Constants.BASE_URL.removeSuffix("/")
        val url = "$baseUrl/uploads/$videoFile"

        binding.tvLabelVideo.visibility = View.VISIBLE
        binding.playerVideo.visibility = View.VISIBLE

        player = androidx.media3.exoplayer.ExoPlayer.Builder(requireContext()).build().apply {
            binding.playerVideo.player = this
            setMediaItem(androidx.media3.common.MediaItem.fromUri(url))
            prepare()
            playWhenReady = false
        }

        binding.playerVideo.setFullscreenButtonClickListener { isFullscreen ->
            toggleFullscreen(isFullscreen)
        }
    }

    private var contenedorOriginal: ViewGroup? = null
    private var indiceOriginal: Int = -1
    private var paramsOriginales: ViewGroup.LayoutParams? = null

    private fun toggleFullscreen(isFullscreen: Boolean) {
        val activity = requireActivity()
        val decor = activity.window.decorView as ViewGroup

        if (isFullscreen) {
            originalSystemUi = activity.window.decorView.systemUiVisibility
            contenedorOriginal = binding.playerVideo.parent as? ViewGroup
            indiceOriginal = contenedorOriginal?.indexOfChild(binding.playerVideo) ?: -1
            paramsOriginales = binding.playerVideo.layoutParams
            contenedorOriginal?.removeView(binding.playerVideo)
            binding.playerVideo.setBackgroundColor(android.graphics.Color.BLACK)
            decor.addView(
                binding.playerVideo, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            activity.window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        } else {
            decor.removeView(binding.playerVideo)
            binding.playerVideo.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            contenedorOriginal?.addView(binding.playerVideo, indiceOriginal, paramsOriginales)
            activity.window.decorView.systemUiVisibility = originalSystemUi
        }
        enFullscreen = isFullscreen
    }

    private fun mostrarFrame360() {
        if (imagenes360.isEmpty()) return
        val idx = ((frame360 % imagenes360.size) + imagenes360.size) % imagenes360.size
        val baseUrl = com.example.motos.utils.Constants.BASE_URL.removeSuffix("/")
        val url = "$baseUrl/uploads/imagenes/${imagenes360[idx].url}"
        Glide.with(this)
            .load(url)
            .dontAnimate()
            .placeholder(binding.ivPrincipal.drawable)
            .into(binding.ivPrincipal)
    }

    private fun toggle360() {
        modo360 = !modo360
        if (modo360) {
            binding.btnToggle360.text = "GALERÍA"
            binding.viewPagerGaleria.visibility = View.GONE
            binding.ivPrincipal.visibility = View.VISIBLE
            binding.tvHint360.visibility = View.VISIBLE
            frame360 = 0
            mostrarFrame360()
        } else {
            binding.btnToggle360.text = "360°"
            binding.viewPagerGaleria.visibility = View.VISIBLE
            binding.ivPrincipal.visibility = View.GONE
            binding.tvHint360.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configurarTouch360() {
        var inicioX = 0f
        var frameInicio = 0
        binding.ivPrincipal.setOnTouchListener { _, event ->
            if (!modo360 || imagenes360.isEmpty()) return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    inicioX = event.x
                    frameInicio = frame360
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.x - inicioX
                    val sensibilidad = binding.ivPrincipal.width / imagenes360.size.toFloat()
                    val delta = (deltaX / sensibilidad).toInt()
                    val nuevo = frameInicio - delta
                    if (nuevo != frame360) {
                        frame360 = nuevo
                        mostrarFrame360()
                    }
                    true
                }

                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
        _binding = null
    }
}