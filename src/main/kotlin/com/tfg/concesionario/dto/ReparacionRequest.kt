package com.tfg.concesionario.dto

import java.time.LocalDate

data class ReparacionRequest(
    val citaId: Long,
    val mecanicoId: Long,
    val descripcion: String,
    val fecha: LocalDate = LocalDate.now(),
    val estado: String = "PENDIENTE"
)