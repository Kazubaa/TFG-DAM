package com.example.motos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.motos.R

class GaleriaAdapter(private val urls: List<String>) : RecyclerView.Adapter<GaleriaAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.ivGaleriaItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_galeria_imagen, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(holder.imageView.context)
            .load(urls[position])
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount() = urls.size
}