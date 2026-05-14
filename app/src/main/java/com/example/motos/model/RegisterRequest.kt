package com.example.motos.model

data class RegisterRequest(
    val username: String,
    val password: String,
    val rol: String = "CLIENTE",
    val email: String
)
