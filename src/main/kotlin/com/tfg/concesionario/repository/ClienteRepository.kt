package com.tfg.concesionario.repository

import com.tfg.concesionario.model.Cliente
import org.springframework.data.jpa.repository.JpaRepository

interface ClienteRepository : JpaRepository<Cliente, Long> {
    fun findByEmail(email: String): Cliente?
}