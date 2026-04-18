package com.example.motos.model

data class ReservaRequest(
    val clienteId: Long,
    val motoSegundaManoId: Long,
    val fecha: String,
    val hora: String,
    val estado: String = "PENDIENTE"
)