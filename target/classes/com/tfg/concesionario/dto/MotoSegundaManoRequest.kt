package com.tfg.concesionario.dto

data class MotoSegundaManoRequest(
    val marca: String,
    val modelo: String,
    val precio: Double,
    val cilindrada: Int,
    val km: Int,
    val cv: Int,
    val matricula: String,
    val imagenPrincipal: String? = null
)
