package com.example.motos.model

data class Reparacion(
    val id: Long,
    val cita: Cita,
    val mecanico: MecanicoSimple,
    val descripcion: String,
    val fecha: String,
    val items: List<ItemReparacion>,
    val subtotal: Double,
    val iva: Double,
    val total: Double,
    val estado: String
)

data class ItemReparacion(
    val id: Long = 0,
    val descripcion: String,
    val tipo: String,
    val cantidad: Double,
    val precioUnitario: Double
)

data class ReparacionRequest(
    val citaId: Long,
    val mecanicoId: Long,
    val descripcion: String,
    val items: List<ItemReparacionRequest>,
    val estado: String = "BORRADOR"
)

data class ItemReparacionRequest(
    val descripcion: String,
    val tipo: String,
    val cantidad: Double,
    val precioUnitario: Double
)