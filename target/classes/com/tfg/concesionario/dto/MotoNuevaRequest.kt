package com.tfg.concesionario.dto

data class MotoNuevaRequest(
    val marca: String,
    val modelo: String,
    val categoria: String,
    val anio: Int,
    val precio: Double,
    val cilindrada: Int,
    val cv: Int,
    val peso: Int,
    val descripcion: String,
    val videoFile: String? = null
)