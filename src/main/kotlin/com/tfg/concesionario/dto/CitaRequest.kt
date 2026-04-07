package com.tfg.concesionario.dto

import java.time.LocalDate
import java.time.LocalTime

data class CitaRequest(
    val clienteId: Long,
    val mecanicoId: Long? = null,
    val fecha: LocalDate,
    val hora: LocalTime,
    val estado: String = "PENDIENTE"
)