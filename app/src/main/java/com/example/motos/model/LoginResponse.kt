package com.example.motos.model

data class LoginResponse(
    val token: String,
    val id: Long,
    val username: String,
    val rol: String
)
