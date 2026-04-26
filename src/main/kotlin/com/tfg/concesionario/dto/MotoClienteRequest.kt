package com.tfg.concesionario.dto

data class MotoClienteRequest(
    val matricula: String,
    val marca: String,
    val modelo: String,
    val km: Int,
    val clienteId: Long
)