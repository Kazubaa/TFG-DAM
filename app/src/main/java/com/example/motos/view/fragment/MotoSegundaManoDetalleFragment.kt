package com.example.motos.view.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.motos.adapter.ImagenDetalleAdapter
import com.example.motos.databinding.FragmentMotoSegundaManoDetalleBinding
import com.example.motos.model.ImagenMoto
import com.example.motos.model.MotoSegundaManoRequest
import com.example.motos.model.ReservaRequest
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.MotoSegundaManoRepository
import com.example.motos.utils.Constants
import com.example.motos.utils.SessionManager
import com.example.motos.viewmodel.MotoSegundaManoViewModel
import com.example.motos.viewmodel.MotoSegundaManoViewModelFactory
import com.example.motos.viewmodel.ReservaState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Calendar

class MotoSegundaManoDetalleFragment : Fragment() {

    private var _binding: FragmentMotoSegundaManoDetalleBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager
    private lateinit var imagenAdapter: ImagenDetalleAdapter
    private var motoId: Long = -1
    private var disponible: Boolean = true
    private var imagenes: MutableList<ImagenMoto> = mutableListOf()
    private var reservaId: Long? = null

    private val viewModel: MotoSegundaManoViewModel by viewModels {
        MotoSegundaManoViewModelFactory(
            MotoSegundaManoRepository(RetrofitClient.getInstance(requireContext()))
        )
    }

    // Selector de imágenes múltiple
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uris = mutableListOf<Uri>()

            if (data?.clipData != null) {
                for (i in 0 until data.clipData!!.itemCount) {
                    uris.add(data.clipData!!.getItemAt(i).uri)
                }
            } else if (data?.data != null) {
                uris.add(data.data!!)
            }

            uris.forEach { uri -> subirImagen(uri) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMotoSegundaManoDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SessionManager(requireContext())
        val rol = session.getRol() ?: "INVITADO"

        // Cargar datos de arguments
        motoId = arguments?.getLong("motoId") ?: -1
        disponible = arguments?.getBoolean("disponible") ?: true
        val marca = arguments?.getString("marca") ?: ""
        val modelo = arguments?.getString("modelo") ?: ""
        val precio = arguments?.getDouble("precio") ?: 0.0
        val cilindrada = arguments?.getInt("cilindrada") ?: 0
        val cv = arguments?.getInt("cv") ?: 0
        val km = arguments?.getInt("km") ?: 0
        val matricula = arguments?.getString("matricula") ?: ""
        val imagenPrincipal = arguments?.getString("imagenPrincipal")

        // Mostrar datos
        binding.tvMarca.text = marca
        binding.tvModelo.text = modelo
        binding.tvPrecio.text = "%.0f €".format(precio)
        binding.tvCilindrada.text = cilindrada.toString()
        binding.tvCv.text = cv.toString()
        binding.tvKm.text = km.toString()
        binding.tvMatricula.text = matricula
        binding.tvDisponible.text = if (disponible) "Disponible" else "Reservada"
        binding.tvDisponible.setBackgroundResource(
            if (disponible) android.R.color.holo_green_dark
            else android.R.color.holo_red_dark
        )

//        if (imagenPrincipal != null) {
//            Glide.with(this)
//                .load("${Constants.BASE_URL}imagenes/$imagenPrincipal")
//                .centerCrop()
//                .into(binding.ivPrincipal)
//        }

        // Cargar galería
        viewModel.cargarImagenes(motoId)
        setupGaleria(rol)

        // Configurar por rol
        when (rol) {
            "CLIENTE" -> setupCliente()
            "ADMIN", "VENDEDOR" -> setupAdminVendedor(marca, modelo, precio, cilindrada, cv, km, matricula)
        }

        binding.btnVolver.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        observeViewModel()
    }

    private fun setupGaleria(rol: String) {
        val puedeEliminar = rol == "ADMIN" || rol == "VENDEDOR"

        imagenAdapter = ImagenDetalleAdapter(
            items = emptyList(),
            onImagenClick = { imagen ->
                mostrarImagenFullscreen(imagen.url)
            },
            onEliminarClick = if (puedeEliminar) { imagen ->
                confirmarEliminarImagen(imagen)
            } else null
        )

        binding.rvImagenes.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imagenAdapter
        }

        // RV se comporta como un ViewPager
        androidx.recyclerview.widget.PagerSnapHelper().attachToRecyclerView(binding.rvImagenes)
    }

    private fun mostrarImagenFullscreen(url: String) {
        val dialog = android.app.Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val view = layoutInflater.inflate(com.example.motos.R.layout.dialog_imagen_fullscreen, null)

        val ivFullscreen = view.findViewById<android.widget.ImageView>(com.example.motos.R.id.ivFullscreen)
        val root = view.findViewById<android.widget.FrameLayout>(com.example.motos.R.id.dialogRoot)

        Glide.with(this)
            .load("${Constants.BASE_URL}imagenes/$url")
            .fitCenter()
            .into(ivFullscreen)

        root.setOnClickListener { dialog.dismiss() }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun setupCliente() {
        if (disponible) {
            binding.btnReservar.visibility = View.VISIBLE
            binding.btnReservar.setOnClickListener { mostrarDatePicker() }
        } else {
            binding.btnReservar.visibility = View.VISIBLE
            binding.btnReservar.isEnabled = false
            binding.btnReservar.text = "RESERVADA"
            binding.btnReservar.backgroundTintList =
                android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY)
        }
    }

    private fun setupAdminVendedor(
        marca: String, modelo: String, precio: Double,
        cilindrada: Int, cv: Int,km: Int, matricula: String
    ) {
        android.util.Log.d("ADMIN_SETUP", "Entrando en setupAdminVendedor")
        binding.fabEditar.visibility = View.VISIBLE
        binding.btnAnadirImagen.visibility = View.VISIBLE

        binding.etMarca.setText(marca)
        binding.etModelo.setText(modelo)
        binding.etPrecio.setText(precio.toInt().toString())
        binding.etCilindrada.setText(cilindrada.toString())
        binding.etKm.setText(km.toString())
        binding.etCv.setText(cv.toString())
        binding.etMatricula.setText(matricula)

        // Mostrar/ocultar panel de edición
        binding.fabEditar.setOnClickListener {
            val visible = binding.layoutEdicion.visibility == View.VISIBLE
            binding.layoutEdicion.visibility = if (visible) View.GONE else View.VISIBLE
        }

        binding.btnGuardar.setOnClickListener {
            val request = MotoSegundaManoRequest(
                marca = binding.etMarca.text.toString().trim(),
                modelo = binding.etModelo.text.toString().trim(),
                precio = binding.etPrecio.text.toString().toDoubleOrNull() ?: precio,
                cilindrada = binding.etCilindrada.text.toString().toIntOrNull() ?: cilindrada,
                km = binding.etKm.text.toString().toIntOrNull() ?: km,
                cv = binding.etCv.text.toString().toIntOrNull() ?: cv,
                matricula = binding.etMatricula.text.toString().trim()
            )
            viewModel.actualizarMoto(motoId, request)
        }

        binding.btnEliminarMoto.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar moto")
                .setMessage("¿Estás seguro de que quieres eliminar esta moto?")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.eliminarMoto(motoId)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.btnAnadirImagen.setOnClickListener {
            abrirGaleria()
        }

        if (!disponible) {
            viewModel.cargarReservaActiva(motoId)

        }
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply { set(year, month, day) }
                val dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK)
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    Toast.makeText(context, "Solo se puede reservar de lunes a viernes", Toast.LENGTH_LONG).show()
                    return@DatePickerDialog
                }
                val fecha = "%04d-%02d-%02d".format(year, month + 1, day)
                mostrarTimePicker(fecha)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun mostrarTimePicker(fecha: String) {
        val horas = listOf(
            "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
            "16:00", "17:00", "18:00", "19:00"
        )
        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona hora")
            .setItems(horas.toTypedArray()) { _, which ->
                val hora = "${horas[which]}:00"
                val clienteId = session.getClienteId()
                if (clienteId == -1L) {
                    Toast.makeText(context, "Debes iniciar sesión como cliente", Toast.LENGTH_SHORT).show()
                    return@setItems
                }
                viewModel.crearReserva(clienteId, motoId, fecha, hora)
            }
            .show()
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun subirImagen(uri: Uri) {
        val context = requireContext()
        val inputStream = context.contentResolver.openInputStream(uri) ?: return
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { inputStream.copyTo(it) }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val motoIdBody = motoId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        viewModel.subirImagen(motoIdBody, body)
    }

    private fun confirmarEliminarImagen(imagen: ImagenMoto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar imagen")
            .setMessage("¿Eliminar esta imagen?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarImagen(imagen.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.imagenes.observe(viewLifecycleOwner) { imgs ->
            imagenes = imgs.toMutableList()
            imagenAdapter.updateList(imgs)
        }

        viewModel.reservaState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ReservaState.Success -> {
                    Toast.makeText(context, "Solicitud enviada", Toast.LENGTH_SHORT).show()
                    binding.btnReservar.isEnabled = false
                    binding.btnReservar.text = "PENDIENTE"
                    binding.btnReservar.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY)
                }
                is ReservaState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        viewModel.reservaActiva.observe(viewLifecycleOwner) { reserva ->
            if (reserva != null && !disponible) {
                binding.btnCancelarReserva.visibility = View.VISIBLE
                binding.btnCancelarReserva.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Cancelar reserva")
                        .setMessage("La moto volverá a estar disponible. ¿Continuar?")
                        .setPositiveButton("Sí, cancelar") { _, _ ->
                            viewModel.actualizarEstadoReserva(reserva.id, "CANCELADA")
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            }
        }

        viewModel.updateState.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Moto actualizada correctamente", Toast.LENGTH_SHORT).show()
                binding.layoutEdicion.visibility = View.GONE
                parentFragmentManager.popBackStack()
            }
        }

        viewModel.deleteState.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Moto eliminada", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}