package com.example.motos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.motos.databinding.ItemImagenDetalleBinding
import com.example.motos.model.ImagenMoto
import com.example.motos.utils.Constants

class ImagenDetalleAdapter(
    private var items: List<ImagenMoto>,
    private val onImagenClick: (ImagenMoto) -> Unit,
    private val onEliminarClick: ((ImagenMoto) -> Unit)? = null
) : RecyclerView.Adapter<ImagenDetalleAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemImagenDetalleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImagenDetalleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imagen = items[position]
        Glide.with(holder.itemView.context)
            .load("${Constants.BASE_URL}imagenes/${imagen.url}")
            .centerCrop()
            .into(holder.binding.ivImagen)

        holder.binding.ivImagen.setOnClickListener { onImagenClick(imagen) }

        if (onEliminarClick != null) {
            holder.binding.btnEliminar.visibility = android.view.View.VISIBLE
            holder.binding.btnEliminar.setOnClickListener { onEliminarClick.invoke(imagen) }
        } else {
            holder.binding.btnEliminar.visibility = android.view.View.GONE
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<ImagenMoto>) {
        items = newItems
        notifyDataSetChanged()
    }
}