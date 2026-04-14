package com.example.motos.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.motos.databinding.ItemPromocionBinding
import com.example.motos.utils.Constants

class PromocionAdapter(private var items: List<String>) :
    RecyclerView.Adapter<PromocionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPromocionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPromocionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = "${Constants.BASE_URL}${items[position]}"
        Glide.with(holder.itemView.context)
            .load(url)
            .centerCrop()
            .into(holder.binding.ivPromocion)
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }
}