package com.tfg.concesionario.repository

import com.tfg.concesionario.model.Mecanico
import org.springframework.data.jpa.repository.JpaRepository

interface MecanicoRepository : JpaRepository<Mecanico, Long> {
    fun findByEmail(email: String): Mecanico?
}