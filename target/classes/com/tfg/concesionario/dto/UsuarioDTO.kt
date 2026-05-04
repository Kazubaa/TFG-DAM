package com.tfg.concesionario.dto

import com.tfg.concesionario.model.Rol

data class UsuarioDTO(
    val id: Long,
    val username: String,
    val rol: Rol,
    val clienteId: Long? = null
)