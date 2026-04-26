package com.example.motos.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.adapter.ItemPresupuestoAdapter
import com.example.motos.databinding.DialogAnadirItemBinding
import com.example.motos.databinding.FragmentCitaDetalleBinding
import com.example.motos.model.Cita
import com.example.motos.model.CitaRequest
import com.example.motos.model.ItemReparacion
import com.example.motos.model.ItemReparacionRequest
import com.example.motos.model.Reparacion
import com.example.motos.model.ReparacionRequest
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.TallerRepository
import com.example.motos.utils.SessionManager
import com.example.motos.viewmodel.TallerViewModel
import com.example.motos.viewmodel.TallerViewModelFactory
import kotlinx.coroutines.launch
import java.util.Calendar


class CitaDetalleFragment : Fragment() {

    private var _binding: FragmentCitaDetalleBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager
    private lateinit var itemAdapter: ItemPresupuestoAdapter
    private var citaActual: Cita? = null
    private var reparacionActual: Reparacion? = null
    private var citaId: Long = -1

    private val viewModel: TallerViewModel by viewModels {
        TallerViewModelFactory(TallerRepository(RetrofitClient.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitaDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SessionManager(requireContext())
        val rol = session.getRol() ?: "INVITADO"
        val clienteId = session.getClienteId()

        citaId = arguments?.getLong("citaId") ?: -1

        binding.btnVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        setupRecycler(rol)

        viewModel.citaActual.observe(viewLifecycleOwner) { cita ->
            if (cita != null) {
                citaActual = cita
                mostrarCita(cita, rol)
            }
        }

        viewModel.reparacionActual.observe(viewLifecycleOwner) { rep ->
            reparacionActual = rep
            if (rep != null) {
                itemAdapter.updateList(rep.items)
                binding.tvSubtotal.text = "%.2f €".format(rep.subtotal)
                binding.tvIva.text = "%.2f €".format(rep.iva)
                binding.tvTotal.text = "%.2f €".format(rep.total)
            } else {
                actualizarTotales()
            }
            configurarBotones(rol)
        }

        viewModel.accionCompleta.observe(viewLifecycleOwner) {
            viewModel.cargarCitaYReparacion(citaId, rol, clienteId)
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (msg != null) Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

        viewModel.cargarCitaYReparacion(citaId, rol, clienteId)
    }

    private fun setupRecycler(rol: String) {
        val esMecanicoOAdmin = rol == "MECANICO" || rol == "ADMIN"
        itemAdapter = ItemPresupuestoAdapter(
            items = mutableListOf(),
            permitirEliminar = esMecanicoOAdmin,
            onEliminar = { pos ->
                itemAdapter.removeAt(pos)
                actualizarTotales()
            }
        )
        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }
    }

    private fun mostrarCita(cita: Cita, rol: String) {
        binding.tvTipo.text = cita.tipo
        binding.tvMoto.text = cita.motoCliente?.let {
            "${it.marca} ${it.modelo} (${it.matricula})"
        } ?: "Sin moto"
        binding.tvFechaHora.text = "${cita.fecha} a las ${cita.hora}"
        binding.tvDescripcion.text = cita.descripcion ?: "(sin descripción)"

        binding.tvEstado.text = cita.estado
        binding.tvEstado.setTextColor(Color.WHITE)
        binding.tvEstado.setBackgroundColor(colorEstado(cita.estado))

        if (rol != "CLIENTE") {
            binding.tvCliente.visibility = View.VISIBLE
            binding.tvCliente.text = "Cliente: ${cita.cliente.nombre}"
        }

        // Mostrar checks solo si es MANTENIMIENTO y mecánico y no hay reparación aún
        if (cita.tipo == "MANTENIMIENTO"
            && (rol == "MECANICO" || rol == "ADMIN")
            && cita.estado == "ACEPTADA"
            && reparacionActual == null
        ) {
            binding.layoutMantenimientoFijos.visibility = View.VISIBLE
            setupChecks()
        } else {
            binding.layoutMantenimientoFijos.visibility = View.GONE
        }
    }

    private fun setupChecks() {
        binding.cbFiltroAire.setOnCheckedChangeListener { _, isChecked ->
            toggleItemFijo("Filtro de aire", isChecked)
        }
        binding.cbBujias.setOnCheckedChangeListener { _, isChecked ->
            toggleItemFijo("Bujías", isChecked)
        }
        binding.cbAceite.setOnCheckedChangeListener { _, isChecked ->
            toggleItemFijo("Cambio de aceite", isChecked)
        }
        binding.cbFrenos.setOnCheckedChangeListener { _, isChecked ->
            toggleItemFijo("Pastillas de freno", isChecked)
        }
    }

    private fun toggleItemFijo(descripcion: String, marcado: Boolean) {
        val existentes = itemAdapter.getItems().toMutableList()
        if (marcado) {
            mostrarDialogoPrecio(descripcion)
        } else {
            val idx = existentes.indexOfFirst { it.descripcion == descripcion }
            if (idx >= 0) {
                itemAdapter.removeAt(idx)
                actualizarTotales()
            }
        }
    }

    private fun mostrarDialogoPrecio(descripcion: String) {
        val dialogBinding = DialogAnadirItemBinding.inflate(layoutInflater)
        dialogBinding.etDescripcion.setText(descripcion)
        dialogBinding.etDescripcion.isEnabled = false
        dialogBinding.rbTarea.isChecked = true

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Añadir") { _, _ ->
                val cantidad = dialogBinding.etCantidad.text.toString().toDoubleOrNull() ?: 1.0
                val precio = dialogBinding.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
                itemAdapter.addItem(ItemReparacion(
                    descripcion = descripcion,
                    tipo = "TAREA",
                    cantidad = cantidad,
                    precioUnitario = precio
                ))
                actualizarTotales()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                // desmarcar el check si cancela
                when (descripcion) {
                    "Filtro de aire" -> binding.cbFiltroAire.isChecked = false
                    "Bujías" -> binding.cbBujias.isChecked = false
                    "Cambio de aceite" -> binding.cbAceite.isChecked = false
                    "Pastillas de freno" -> binding.cbFrenos.isChecked = false
                }
            }
            .show()
    }

    private fun mostrarDialogoAnadirCustom() {
        val dialogBinding = DialogAnadirItemBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Añadir") { _, _ ->
                val descripcion = dialogBinding.etDescripcion.text.toString().trim()
                val cantidad = dialogBinding.etCantidad.text.toString().toDoubleOrNull() ?: 0.0
                val precio = dialogBinding.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
                val tipo = when {
                    dialogBinding.rbManoObra.isChecked -> "MANO_OBRA"
                    dialogBinding.rbPieza.isChecked -> "PIEZA"
                    else -> "TAREA"
                }

                if (descripcion.isEmpty() || cantidad <= 0 || precio < 0) {
                    Toast.makeText(context, "Completa los campos correctamente", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                itemAdapter.addItem(ItemReparacion(
                    descripcion = descripcion,
                    tipo = tipo,
                    cantidad = cantidad,
                    precioUnitario = precio
                ))
                actualizarTotales()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarTotales() {
        val subtotal = itemAdapter.getItems().sumOf { it.cantidad * it.precioUnitario }
        val iva = subtotal * 0.21
        val total = subtotal + iva
        binding.tvSubtotal.text = "%.2f €".format(subtotal)
        binding.tvIva.text = "%.2f €".format(iva)
        binding.tvTotal.text = "%.2f €".format(total)
    }

    private fun configurarBotones(rol: String) {
        val cita = citaActual ?: return
        val rep = reparacionActual

        binding.btnAnadirItem.visibility = View.GONE
        binding.btnEnviarPresupuesto.visibility = View.GONE
        binding.layoutClienteAccion.visibility = View.GONE
        binding.layoutAccionesCita.visibility = View.GONE
        binding.btnEntregar.visibility = View.GONE
        binding.btnConfirmarRecogida.visibility = View.GONE

        // Cancelar / modificar / eliminar solo si está PENDIENTE
        if (cita.estado == "PENDIENTE") {
            binding.layoutAccionesCita.visibility = View.VISIBLE

            if (rol == "ADMIN" || rol == "MECANICO") {
                binding.btnEliminarCita.visibility = View.VISIBLE
                binding.btnEliminarCita.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Eliminar cita")
                        .setMessage("Se borrará de la base de datos permanentemente")
                        .setPositiveButton("Eliminar") { _, _ ->
                            viewModel.eliminarCita(cita.id)
                            parentFragmentManager.popBackStack()
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
            }

            binding.btnCancelarCita.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Cancelar cita")
                    .setMessage("La cita pasará a estado CANCELADA")
                    .setPositiveButton("Sí") { _, _ ->
                        viewModel.actualizarEstadoCita(cita.id, "CANCELADA")
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            binding.btnModificarCita.setOnClickListener {
                mostrarDialogoModificar(cita)
            }
        }

        when (rol) {
            "MECANICO", "ADMIN" -> {
                if (cita.estado == "ACEPTADA" && (rep == null || rep.estado == "BORRADOR")) {
                    binding.btnAnadirItem.visibility = View.VISIBLE
                    binding.btnAnadirItem.setOnClickListener { mostrarDialogoAnadirCustom() }

                    binding.btnEnviarPresupuesto.visibility = View.VISIBLE
                    binding.btnEnviarPresupuesto.setOnClickListener { enviarPresupuesto() }
                }

                if (rep?.estado == "TALLER") {
                    binding.btnEntregar.visibility = View.VISIBLE
                    binding.btnEntregar.setOnClickListener {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Lista para recoger")
                            .setMessage("¿La moto está lista? El cliente recibirá una notificación")
                            .setPositiveButton("Sí, lista") { _, _ ->
                                viewModel.actualizarEstadoReparacion(rep.id, "RECOGER")
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                }
            }
            "CLIENTE" -> {
                if (rep?.estado == "ENVIADO") {
                    binding.layoutClienteAccion.visibility = View.VISIBLE
                    binding.btnAprobar.setOnClickListener {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Aprobar presupuesto")
                            .setMessage("¿Aprobar el presupuesto? La moto pasará a estado TALLER")
                            .setPositiveButton("Sí, aprobar") { _, _ ->
                                viewModel.actualizarEstadoReparacion(rep.id, "TALLER")
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                    binding.btnRechazar.setOnClickListener {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Rechazar presupuesto")
                            .setMessage("¿Rechazar el presupuesto?")
                            .setPositiveButton("Sí, rechazar") { _, _ ->
                                viewModel.actualizarEstadoReparacion(rep.id, "RECHAZADO")
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                }

                if (rep?.estado == "RECOGER") {
                    binding.btnConfirmarRecogida.visibility = View.VISIBLE
                    binding.btnConfirmarRecogida.setOnClickListener {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Confirmar recogida")
                            .setMessage("¿Confirmar que has recogido la moto? La reparación se cerrará como COMPLETADA")
                            .setPositiveButton("Sí, he recogido") { _, _ ->
                                viewModel.actualizarEstadoReparacion(rep.id, "COMPLETADO")
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                }
            }
        }
    }

    private fun mostrarDialogoModificar(cita: Cita) {
        // Paso 1: DatePicker
        val calendar = Calendar.getInstance()
        val datePicker = android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply { set(year, month, day) }
                val dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK)
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    Toast.makeText(context, "Solo de lunes a viernes", Toast.LENGTH_LONG).show()
                    return@DatePickerDialog
                }
                val fecha = "%04d-%02d-%02d".format(year, month + 1, day)
                seleccionarHoraModificar(cita, fecha)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun seleccionarHoraModificar(cita: Cita, fecha: String) {
        val horas = listOf(
            "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
            "16:00", "17:00", "18:00", "19:00"
        )
        AlertDialog.Builder(requireContext())
            .setTitle("Nueva hora")
            .setItems(horas.toTypedArray()) { _, which ->
                val hora = "${horas[which]}:00"
                viewLifecycleOwner.lifecycleScope.launch {
                    val disponible = viewModel.comprobarDisponibilidad(fecha, hora)
                    if (!disponible) {
                        Toast.makeText(context, "No hay mecánicos disponibles", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    viewModel.actualizarCita(
                        cita.id,
                        CitaRequest(
                            clienteId = cita.cliente.id,
                            motoMatricula = cita.motoCliente?.matricula,
                            fecha = fecha,
                            hora = hora,
                            tipo = cita.tipo,
                            descripcion = cita.descripcion,
                            estado = cita.estado
                        )
                    )
                }
            }
            .show()
    }

    private fun enviarPresupuesto() {
        val cita = citaActual ?: return
        val items = itemAdapter.getItems()
        if (items.isEmpty()) {
            Toast.makeText(context, "Añade al menos un concepto", Toast.LENGTH_SHORT).show()
            return
        }
        val mecanicoId = cita.mecanico?.id
        if (mecanicoId == null) {
            Toast.makeText(context, "La cita no tiene mecánico asignado", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Enviar presupuesto")
            .setMessage("¿Enviar presupuesto al cliente?")
            .setPositiveButton("Enviar") { _, _ ->
                val request = ReparacionRequest(
                    citaId = cita.id,
                    mecanicoId = mecanicoId,
                    descripcion = "",
                    items = items.map {
                        ItemReparacionRequest(it.descripcion, it.tipo, it.cantidad, it.precioUnitario)
                    },
                    estado = "ENVIADO"
                )

                if (reparacionActual == null) {
                    viewModel.crearReparacion(request)
                } else {
                    // Si ya existe como BORRADOR, actualizarla
                    viewModel.actualizarReparacion(reparacionActual!!.id, request)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun colorEstado(estado: String): Int = when (estado) {
        "PENDIENTE" -> Color.parseColor("#FF9800")
        "ACEPTADA" -> Color.parseColor("#4CAF50")
        "RECHAZADA" -> Color.parseColor("#F44336")
        "COMPLETADA" -> Color.parseColor("#2196F3")
        else -> Color.GRAY
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}