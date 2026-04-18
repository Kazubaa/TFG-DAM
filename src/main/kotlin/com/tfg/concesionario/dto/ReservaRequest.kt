package com.tfg.concesionario.dto

import java.time.LocalDate
import java.time.LocalTime

data class ReservaRequest(
    val clienteId: Long,
    val motoId: Long? = null,
    val motoSegundaManoId: Long? = null,
    val fecha: LocalDate,
    val hora: LocalTime,
    val estado: String = "PENDIENTE"
)