package com.example.motos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motos.databinding.ItemMotoClienteBinding
import com.example.motos.model.MotoCliente

class MotoClienteAdapter(
    private var items: List<MotoCliente>,
    private val onClick: (MotoCliente) -> Unit,
    private val onEliminar: (MotoCliente) -> Unit,
    private val mostrarEliminar: Boolean = true
) : RecyclerView.Adapter<MotoClienteAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMotoClienteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMotoClienteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val moto = items[position]
        with(holder.binding) {
            tvMarcaModelo.text = "${moto.marca} ${moto.modelo}"
            tvMatricula.text = moto.matricula
            tvKm.text = "${moto.km} km"
            root.setOnClickListener { onClick(moto) }
            btnEliminar.visibility = if (mostrarEliminar) View.VISIBLE else View.GONE
            btnEliminar.setOnClickListener { onEliminar(moto) }
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<MotoCliente>) {
        items = newItems
        notifyDataSetChanged()
    }
}