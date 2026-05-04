package com.example.motos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.motos.databinding.ItemImagenThumbBinding
import com.example.motos.model.ImagenMotoNueva
import com.example.motos.utils.Constants

class ImagenMotoNuevaAdapter(
    private var items: List<ImagenMotoNueva>,
    private val onEliminar: (ImagenMotoNueva) -> Unit
) : RecyclerView.Adapter<ImagenMotoNuevaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemImagenThumbBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImagenThumbBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val img = items[position]
        with(holder.binding) {
            tvOrden.text = "Orden ${img.orden + 1}"
            val baseUrl = Constants.BASE_URL.removeSuffix("/")
            Glide.with(root.context)
                .load("$baseUrl/uploads/imagenes/${img.url}")
                .into(ivThumb)
            btnBorrar.setOnClickListener { onEliminar(img) }
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<ImagenMotoNueva>) {
        items = newItems
        notifyDataSetChanged()
    }
}