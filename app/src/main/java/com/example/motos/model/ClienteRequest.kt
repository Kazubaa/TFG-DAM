package com.example.motos.model

data class ClienteRequest(
    val nombre: String,
    val email: String,
    val telefono: String
)

data class Cliente(
    val id: Long,
    val nombre: String,
    val email: String,
    val telefono: String
)