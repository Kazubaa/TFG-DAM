package com.tfg.concesionario.dto

data class RegisterRequest(
    val username: String,
    val password: String,
    val rol: String = "CLIENTE"
)