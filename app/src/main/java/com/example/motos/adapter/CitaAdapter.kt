package com.example.motos.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motos.databinding.ItemCitaBinding
import com.example.motos.model.Cita

class CitaAdapter(
    private var items: List<Cita>,
    private val mostrarCliente: Boolean,
    private val mostrarAcciones: Boolean,
    private val onClick: (Cita) -> Unit,
    private val onAceptar: (Cita) -> Unit = {},
    private val onRechazar: (Cita) -> Unit = {}
) : RecyclerView.Adapter<CitaAdapter.ViewHolder>() {
    var citasConPresupuestoPendiente: Set<Long> = emptySet()

    inner class ViewHolder(val binding: ItemCitaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCitaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cita = items[position]
        with(holder.binding) {
            tvTipo.text = cita.tipo
            tvMoto.text = cita.motoCliente?.let { "${it.marca} ${it.modelo} (${it.matricula})" } ?: "Sin moto"
            tvFechaHora.text = "${cita.fecha} a las ${cita.hora}"
            tvEstado.text = cita.estado
            tvEstado.setTextColor(Color.WHITE)
            tvEstado.setBackgroundColor(
                when (cita.estado) {
                    "PENDIENTE" -> Color.parseColor("#FF9800")
                    "ACEPTADA" -> Color.parseColor("#4CAF50")
                    "RECHAZADA" -> Color.parseColor("#F44336")
                    "COMPLETADA" -> Color.parseColor("#2196F3")
                    else -> Color.GRAY
                }
            )

            if (mostrarCliente) {
                tvCliente.visibility = View.VISIBLE
                tvCliente.text = cita.cliente.nombre
            }

            if (mostrarAcciones && cita.estado == "PENDIENTE") {
                layoutAcciones.visibility = View.VISIBLE
                btnAceptar.setOnClickListener { onAceptar(cita) }
                btnRechazar.setOnClickListener { onRechazar(cita) }
            } else {
                layoutAcciones.visibility = View.GONE
            }


            root.setOnClickListener { onClick(cita) }
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<Cita>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun updateList(newItems: List<Cita>, pendientes: Set<Long> = emptySet()) {
        items = newItems
        citasConPresupuestoPendiente = pendientes
        notifyDataSetChanged()
    }
}