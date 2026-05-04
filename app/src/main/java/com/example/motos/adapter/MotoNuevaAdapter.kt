package com.example.motos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.motos.databinding.ItemMotoNuevaBinding
import com.example.motos.model.MotoNueva
import com.example.motos.utils.Constants

class MotoNuevaAdapter(
    private var items: List<MotoNueva>,
    private var portadas: Map<Long, String>,
    private val onClick: (MotoNueva) -> Unit
) : RecyclerView.Adapter<MotoNuevaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMotoNuevaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMotoNuevaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val moto = items[position]
        with(holder.binding) {
            tvModelo.text = "${moto.marca} ${moto.modelo}"
            tvSpecs.text = "${moto.cilindrada}cc · ${moto.cv} CV · ${moto.peso} kg"
            tvPrecio.text = "%.0f €".format(moto.precio)

            val url = portadas[moto.id]
            if (url != null) {
                val baseUrl = Constants.BASE_URL.removeSuffix("/")
                Glide.with(root.context)
                    .load("$baseUrl/uploads/imagenes/$url")
                    .into(ivFoto)
            } else {
                ivFoto.setImageResource(android.R.color.darker_gray)
            }

            root.setOnClickListener { onClick(moto) }
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<MotoNueva>, newPortadas: Map<Long, String>) {
        items = newItems
        portadas = newPortadas
        notifyDataSetChanged()
    }
}