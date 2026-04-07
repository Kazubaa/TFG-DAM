package com.tfg.concesionario.dto

import com.tfg.concesionario.model.Rol

data class LoginResponse(
    val id: Long,
    val username: String,
    val rol: Rol
)