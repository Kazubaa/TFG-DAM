package com.example.motos.model

data class MotoSegundaManoRequest(
    val marca: String,
    val modelo: String,
    val precio: Double,
    val cilindrada: Int,
    val km: Int,
    val cv: Int,
    val matricula: String
)