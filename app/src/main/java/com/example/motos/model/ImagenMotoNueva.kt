package com.example.motos.model

data class ImagenMotoNueva(
    val id: Long,
    val motoNuevaId: Long,
    val url: String,
    val tipo: String,
    val orden: Int
)