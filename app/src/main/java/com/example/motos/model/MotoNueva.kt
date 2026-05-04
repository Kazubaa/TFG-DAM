package com.example.motos.model

data class MotoNueva(
    val id: Long,
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