package com.example.motos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motos.databinding.ItemMarcaBinding

data class Marca(val nombre: String, val logoResId: Int)

class MarcaAdapter(
    private val items: List<Marca>,
    private val onClick: (Marca) -> Unit
) : RecyclerView.Adapter<MarcaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMarcaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMarcaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val marca = items[position]
        holder.binding.tvNombre.text = marca.nombre
        holder.binding.ivLogo.setImageResource(marca.logoResId)
        holder.binding.root.setOnClickListener { onClick(marca) }
    }

    override fun getItemCount() = items.size
}