package com.example.motos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motos.databinding.ItemHistorialBinding
import com.example.motos.model.Reparacion

class HistorialAdapter(
    private var items: List<Reparacion>,
    private val onClick: (Reparacion) -> Unit
) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHistorialBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistorialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rep = items[position]
        with(holder.binding) {
            tvTipo.text = rep.cita.tipo
            tvFecha.text = rep.fecha
            tvMecanico.text = "Mecánico: ${rep.mecanico.nombre}"
            tvDescripcionItems.text = rep.items.joinToString(", ") { it.descripcion }
            tvTotal.text = "%.2f €".format(rep.total)
            root.setOnClickListener { onClick(rep) }
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<Reparacion>) {
        items = newItems
        notifyDataSetChanged()
    }
}