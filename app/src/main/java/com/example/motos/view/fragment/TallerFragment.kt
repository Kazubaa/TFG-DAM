package com.example.motos.view.fragment

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motos.adapter.CitaAdapter
import com.example.motos.databinding.DialogPedirCitaBinding
import com.example.motos.databinding.FragmentTallerBinding
import com.example.motos.model.Cita
import com.example.motos.model.CitaRequest
import com.example.motos.model.MotoCliente
import com.example.motos.model.Reparacion
import com.example.motos.network.RetrofitClient
import com.example.motos.repository.TallerRepository
import com.example.motos.utils.SessionManager
import com.example.motos.viewmodel.TallerViewModel
import com.example.motos.viewmodel.TallerViewModelFactory
import kotlinx.coroutines.launch
import java.util.Calendar

class TallerFragment : Fragment() {

    private var _binding: FragmentTallerBinding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager
    private lateinit var adapter: CitaAdapter
    private var motosCliente: List<MotoCliente> = emptyList()
    private var notificacionesOcultas = false

    private val viewModel: TallerViewModel by viewModels {
        TallerViewModelFactory(TallerRepository(RetrofitClient.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTallerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        notificacionesOcultas = false
        binding.cardNotificaciones.visibility = View.GONE
        binding.tvNotificaciones.text = ""
        super.onViewCreated(view, savedInstanceState)

        session = SessionManager(requireContext())
        val rol = session.getRol() ?: "INVITADO"
        val clienteId = session.getClienteId()

        val esMecanicoOAdmin = rol == "MECANICO" || rol == "ADMIN"

        adapter = CitaAdapter(
            items = emptyList(),
            mostrarCliente = esMecanicoOAdmin,
            mostrarAcciones = esMecanicoOAdmin,
            onClick = { cita -> abrirDetalleCita(cita) },
            onAceptar = { cita -> aceptarCita(cita) },
            onRechazar = { cita -> viewModel.actualizarEstadoCita(cita.id, "RECHAZADA") }
        )
        binding.rvCitas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@TallerFragment.adapter
        }

        viewModel.citas.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE

            if (rol == "CLIENTE" && clienteId != -1L) {
                viewModel.cargarReparacionesCliente(clienteId)
            } else if (esMecanicoOAdmin) {
                viewModel.cargarReparaciones()
            }
        }

        viewModel.reparaciones.observe(viewLifecycleOwner) { reps ->
            android.util.Log.d("TALLER", "Reparaciones recibidas: ${reps.size}, estados: ${reps.map { it.estado }}")
            if (rol == "CLIENTE") {
                // Marcar citas con presupuesto pendiente ENVIADO
                val pendientes = reps
                    .filter { it.estado == "ENVIADO" }
                    .map { it.cita.id }
                    .toSet()
                adapter.updateList(viewModel.citas.value.orEmpty(), pendientes)

                // Bandeja de  motos listas para recoger
                val entregadas = reps.filter { it.estado == "RECOGER" }
                if (entregadas.isNotEmpty() && !notificacionesOcultas) {
                    binding.cardNotificaciones.visibility = View.VISIBLE
                    binding.tvNotificaciones.text =
                        "Tienes ${entregadas.size} moto(s) lista(s) para recoger"
                    binding.cardNotificaciones.setOnClickListener {
                        mostrarListaNotificaciones(entregadas, "Motos listas para recoger")
                        notificacionesOcultas = true
                        binding.cardNotificaciones.visibility = View.GONE
                    }
                } else {
                    binding.cardNotificaciones.visibility = View.GONE
                }
            } else if (esMecanicoOAdmin) {
                val aprobadas = reps.filter { it.estado == "TALLER" }
                val rechazadas = reps.filter { it.estado == "RECHAZADO" }
                val entregadas = reps.filter { it.estado == "RECOGER" }
                val notificables = aprobadas + rechazadas

                if (notificables.isNotEmpty() && !notificacionesOcultas) {
                    binding.cardNotificaciones.visibility = View.VISIBLE
                    val msg = buildString {
                        if (aprobadas.isNotEmpty()) {
                            append("${aprobadas.size} presupuesto(s) aprobado(s)")
                        }
                        if (rechazadas.isNotEmpty()) {
                            if (isNotEmpty()) append("\n")
                            append("${rechazadas.size} presupuesto(s) rechazado(s)")
                        }
                    }
                    binding.tvNotificaciones.text = msg
                    binding.cardNotificaciones.setOnClickListener {
                        mostrarListaNotificaciones(notificables, "Presupuestos respondidos")
                        notificacionesOcultas = true
                        binding.cardNotificaciones.visibility = View.GONE
                    }
                } else {
                    binding.cardNotificaciones.visibility = View.GONE
                }
            }
        }

        viewModel.motos.observe(viewLifecycleOwner) { list ->
            motosCliente = list
        }

        viewModel.accionCompleta.observe(viewLifecycleOwner) { ok ->
            if (ok == true) {
                cargar(rol, clienteId)
                viewModel.resetAccionCompleta()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                viewModel.resetError()
            }
        }

        binding.btnCerrarNotif.setOnClickListener {
            binding.cardNotificaciones.visibility = View.GONE
            notificacionesOcultas = true
        }

        if (rol == "CLIENTE" && clienteId != -1L) {
            binding.fabPedirCita.visibility = View.VISIBLE
            binding.fabPedirCita.setOnClickListener { pedirCita(clienteId) }
            viewModel.cargarMotosCliente(clienteId)
        }

        cargar(rol, clienteId)
    }

    private fun cargar(rol: String, clienteId: Long) {
        when (rol) {
            "CLIENTE" -> if (clienteId != -1L) viewModel.cargarCitasCliente(clienteId)
            "MECANICO", "ADMIN" -> viewModel.cargarCitas()
        }
    }

    private fun pedirCita(clienteId: Long) {
        if (motosCliente.isEmpty()) {
            Toast.makeText(context, "Primero añade un vehículo en tu perfil", Toast.LENGTH_LONG).show()
            return
        }

        val citasActivas = viewModel.citas.value.orEmpty()
            .filter { it.estado == "PENDIENTE" || it.estado == "ACEPTADA" }
            .mapNotNull { it.motoCliente?.matricula }

        val motosDisponibles = motosCliente.filter { it.matricula !in citasActivas }

        if (motosDisponibles.isEmpty()) {
            Toast.makeText(
                context,
                "Todas tus motos ya tienen una cita activa",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val dialogBinding = DialogPedirCitaBinding.inflate(layoutInflater)

        val motosLabels = motosDisponibles.map { "${it.marca} ${it.modelo} (${it.matricula})" }
        dialogBinding.spinnerMoto.adapter = ArrayAdapter(
            requireContext(), R.layout.simple_spinner_dropdown_item, motosLabels
        )

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Continuar") { _, _ ->
                val motoSeleccionada = motosDisponibles[dialogBinding.spinnerMoto.selectedItemPosition]
                val tipo = if (dialogBinding.rbRevision.isChecked) "REVISION" else "MANTENIMIENTO"
                val descripcion = dialogBinding.etDescripcion.text.toString().trim()
                    .takeIf { it.isNotBlank() }

                seleccionarFechaHora(clienteId, motoSeleccionada, tipo, descripcion)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun seleccionarFechaHora(
        clienteId: Long,
        moto: MotoCliente,
        tipo: String,
        descripcion: String?
    ) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply { set(year, month, day) }
                val dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK)
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    Toast.makeText(context, "Solo de lunes a viernes", Toast.LENGTH_LONG).show()
                    return@DatePickerDialog
                }
                val fecha = "%04d-%02d-%02d".format(year, month + 1, day)
                seleccionarHora(clienteId, moto, tipo, descripcion, fecha)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun seleccionarHora(
        clienteId: Long,
        moto: MotoCliente,
        tipo: String,
        descripcion: String?,
        fecha: String
    ) {
        val horas = listOf(
            "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
            "16:00", "17:00", "18:00", "19:00"
        )
        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona hora")
            .setItems(horas.toTypedArray()) { _, which ->
                val hora = "${horas[which]}:00"
                viewLifecycleOwner.lifecycleScope.launch {
                    val disponible = viewModel.comprobarDisponibilidad(fecha, hora)
                    if (!disponible) {
                        Toast.makeText(
                            context,
                            "No hay mecánicos disponibles a esa hora",
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    }
                    viewModel.crearCita(
                        CitaRequest(
                            clienteId = clienteId,
                            motoMatricula = moto.matricula,
                            fecha = fecha,
                            hora = hora,
                            tipo = tipo,
                            descripcion = descripcion
                        )
                    )
                }
            }
            .show()
    }

    private fun abrirDetalleCita(cita: Cita) {
        val fragment = CitaDetalleFragment().apply {
            arguments = Bundle().apply {
                putLong("citaId", cita.id)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(com.example.motos.R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun aceptarCita(cita: Cita) {
        if (cita.mecanico != null) {
            viewModel.actualizarEstadoCita(cita.id, "ACEPTADA", cita.mecanico.id)
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val mecanicos = viewModel.getMecanicosDisponiblesAhora(cita.fecha, cita.hora)
            if (mecanicos.isEmpty()) {
                Toast.makeText(
                    context,
                    "No hay mecánicos disponibles en esa franja",
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            }

            val labels = mecanicos.map { it.nombre }.toTypedArray()
            AlertDialog.Builder(requireContext())
                .setTitle("Asignar mecánico")
                .setItems(labels) { _, which ->
                    viewModel.actualizarEstadoCita(cita.id, "ACEPTADA", mecanicos[which].id)
                }
                .show()
        }
    }

    private fun mostrarListaNotificaciones(reparaciones: List<Reparacion>, titulo: String) {
        val ordenadas = reparaciones.sortedByDescending { it.fecha }

        val labels = ordenadas.map { rep ->
            val cliente = rep.cita.cliente.nombre
            val moto = rep.cita.motoCliente?.let { "${it.marca} ${it.modelo}" } ?: "Sin moto"
            "[${rep.estado}] $cliente — $moto (%.2f €)".format(rep.total)
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setItems(labels) { _, which ->
                val rep = ordenadas[which]
                abrirDetalleCita(rep.cita)
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}