package com.tfg.concesionario.dto

import java.time.LocalDate

data class ReparacionRequest(
    val citaId: Long,
    val mecanicoId: Long,
    val descripcion: String = "",
    val items: List<ItemReparacionRequest> = emptyList(),
    val estado: String = "BORRADOR"
)

data class ItemReparacionRequest(
    val descripcion: String,
    val tipo: String,
    val cantidad: Double,
    val precioUnitario: Double
)