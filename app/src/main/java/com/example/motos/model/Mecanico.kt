package com.example.motos.model

data class Mecanico(
    val id: Long,
    val nombre: String,
    val email: String,
    val telefono: String,
    val disponible: Boolean = true
)

data class MecanicoRequest(
    val nombre: String,
    val email: String,
    val telefono: String
)