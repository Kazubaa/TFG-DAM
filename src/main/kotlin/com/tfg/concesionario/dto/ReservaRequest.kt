package com.tfg.concesionario.dto

import java.time.LocalDate

data class ReservaRequest(
    val clienteId: Long,
    val motoId: Long? = null,
    val motoSegundaManoId: Long? = null,
    val fecha: LocalDate = LocalDate.now(),
    val estado: String = "PENDIENTE"
)