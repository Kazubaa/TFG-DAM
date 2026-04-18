package com.example.motos.model

data class Reserva(
    val id: Long,
    val cliente: ClienteSimple,
    val motoSegundaMano: MotoSegundaMano?,
    val fecha: String,
    val hora: String,
    val estado: String
)

data class ClienteSimple(
    val id: Long,
    val nombre: String,
    val email: String
)