package com.tfg.concesionario.repository

import com.tfg.concesionario.model.Vendedor
import org.springframework.data.jpa.repository.JpaRepository

interface VendedorRepository : JpaRepository<Vendedor, Long> {
    fun findByEmail(email: String): Vendedor?
}