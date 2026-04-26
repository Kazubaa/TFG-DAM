package com.example.motos.model

data class MotoCliente(
    val matricula: String,
    val marca: String,
    val modelo: String,
    val km: Int
)

data class MotoClienteRequest(
    val matricula: String,
    val marca: String,
    val modelo: String,
    val km: Int,
    val clienteId: Long
)