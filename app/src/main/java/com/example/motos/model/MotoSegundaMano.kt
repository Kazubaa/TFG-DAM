package com.example.motos.model

data class MotoSegundaMano(
    val id: Long,
    val marca: String,
    val modelo: String,
    val precio: Double,
    val cilindrada: Int,
    val cv: Int,
    val km: Int,
    val matricula: String,
    val disponible: Boolean,
    val imagenPrincipal: String?,
    val imagenes: List<ImagenMoto> = emptyList()
)

data class ImagenMoto(
    val id: Long,
    val url: String
)