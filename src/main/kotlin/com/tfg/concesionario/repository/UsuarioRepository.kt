package com.tfg.concesionario.repository

import com.tfg.concesionario.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository

interface UsuarioRepository : JpaRepository<Usuario, Long> {
    fun findByUsername(username: String): Usuario?
}