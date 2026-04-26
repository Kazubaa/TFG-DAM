package com.example.motos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motos.databinding.ItemPresupuestoBinding
import com.example.motos.model.ItemReparacion

class ItemPresupuestoAdapter(
    private var items: MutableList<ItemReparacion>,
    private val permitirEliminar: Boolean,
    private val onEliminar: (Int) -> Unit = {}
) : RecyclerView.Adapter<ItemPresupuestoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPresupuestoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPresupuestoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvDescripcion.text = item.descripcion
            val unidad = when (item.tipo) {
                "MANO_OBRA" -> "h"
                "PIEZA" -> "ud"
                else -> ""
            }
            tvDetalle.text = "${item.cantidad} $unidad × %.2f €".format(item.precioUnitario)
            tvTotal.text = "%.2f €".format(item.cantidad * item.precioUnitario)

            btnEliminar.visibility = if (permitirEliminar) View.VISIBLE else View.GONE
            btnEliminar.setOnClickListener { onEliminar(holder.adapterPosition) }
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<ItemReparacion>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    fun addItem(item: ItemReparacion) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun removeAt(pos: Int) {
        if (pos in items.indices) {
            items.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }

    fun getItems(): List<ItemReparacion> = items.toList()
}