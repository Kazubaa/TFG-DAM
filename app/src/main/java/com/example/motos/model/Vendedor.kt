package com.example.motos.model

data class Vendedor(
    val id: Long,
    val nombre: String,
    val email: String,
    val telefono: String
)

data class VendedorRequest(
    val nombre: String,
    val email: String,
    val telefono: String
)