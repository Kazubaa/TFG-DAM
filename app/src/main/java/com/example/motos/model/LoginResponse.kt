package com.example.motos.model

data class LoginResponse(
    val token: String,
    val id: Long,
    val username: String,
    val rol: String,
    val clienteId: Long? = null,
    val mecanicoId: Long? = null,
    val vendedorId: Long? = null
)
