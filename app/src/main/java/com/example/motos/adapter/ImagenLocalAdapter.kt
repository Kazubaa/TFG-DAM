package com.example.motos.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.motos.databinding.ItemImagenDetalleBinding

class ImagenLocalAdapter(
    private var items: MutableList<Uri>,
    private val onEliminar: (Uri) -> Unit
) : RecyclerView.Adapter<ImagenLocalAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemImagenDetalleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImagenDetalleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = items[position]
        Glide.with(holder.itemView.context)
            .load(uri)
            .centerCrop()
            .into(holder.binding.ivImagen)

        holder.binding.btnEliminar.visibility = View.VISIBLE
        holder.binding.btnEliminar.setOnClickListener { onEliminar(uri) }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<Uri>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }
}