package com.example.motos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motos.databinding.ItemReservaBinding
import com.example.motos.model.Reserva

class ReservaAdapter(
    private var items: List<Reserva>,
    private val mostrarAcciones: Boolean,
    private val onItemClick: (Reserva) -> Unit,
    private val onAceptar: (Reserva) -> Unit = {},
    private val onRechazar: (Reserva) -> Unit = {}
) : RecyclerView.Adapter<ReservaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemReservaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReservaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reserva = items[position]
        with(holder.binding) {
            tvMatricula.text = reserva.motoSegundaMano?.matricula ?: "Sin matrícula"
            tvFechaHora.text = "${reserva.fecha} a las ${reserva.hora}"
            tvEstado.text = reserva.estado
            tvEstado.setBackgroundColor(
                when (reserva.estado) {
                    "PENDIENTE" -> android.graphics.Color.parseColor("#FF9800")
                    "ACEPTADA" -> android.graphics.Color.parseColor("#4CAF50")
                    "RECHAZADA" -> android.graphics.Color.parseColor("#F44336")
                    else -> android.graphics.Color.GRAY
                }
            )
            tvEstado.setTextColor(android.graphics.Color.WHITE)

            if (mostrarAcciones) {
                tvCliente.visibility = View.VISIBLE
                tvCliente.text = reserva.cliente.nombre
                layoutAcciones.visibility = if (reserva.estado == "PENDIENTE") View.VISIBLE else View.GONE
                btnAceptar.setOnClickListener { onAceptar(reserva) }
                btnRechazar.setOnClickListener { onRechazar(reserva) }
            }

            root.setOnClickListener { onItemClick(reserva) }
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<Reserva>) {
        items = newItems
        notifyDataSetChanged()
    }
}