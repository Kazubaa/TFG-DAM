package com.example.motos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.motos.databinding.ItemMotoSegundaManoBinding
import com.example.motos.model.MotoSegundaMano
import com.example.motos.utils.Constants

class MotoSegundaManoAdapter(
    private var items: List<MotoSegundaMano>,
    private val onClick: (MotoSegundaMano) -> Unit
) : RecyclerView.Adapter<MotoSegundaManoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMotoSegundaManoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMotoSegundaManoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val moto = items[position]
        with(holder.binding) {
            tvMarca.text = moto.marca
            tvModelo.text = moto.modelo
            tvPrecio.text = "%.0f €".format(moto.precio)
            tvCilindrada.text = "${moto.cilindrada} cc"
            tvKm.text ="${moto.km} km"
            tvCv.text = "${moto.cv} CV"
            tvMatricula.text = moto.matricula
            tvDisponible.text = if (moto.disponible) "Disponible" else "Reservada"
            tvDisponible.setTextColor(
                if (moto.disponible)
                    holder.itemView.context.getColor(android.R.color.holo_green_dark)
                else
                    holder.itemView.context.getColor(android.R.color.holo_red_dark)
            )

            if (moto.imagenPrincipal != null) {
                Glide.with(holder.itemView.context)
                    .load("${Constants.BASE_URL}imagenes/${moto.imagenPrincipal}")
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivMoto)
            }

            root.setOnClickListener { onClick(moto) }
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<MotoSegundaMano>) {
        items = newItems
        notifyDataSetChanged()
    }
}