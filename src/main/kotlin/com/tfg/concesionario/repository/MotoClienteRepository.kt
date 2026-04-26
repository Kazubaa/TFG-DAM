package com.tfg.concesionario.repository

import com.tfg.concesionario.model.MotoCliente
import org.springframework.data.jpa.repository.JpaRepository

interface MotoClienteRepository : JpaRepository<MotoCliente, String> {
    fun findByClienteId(clienteId: Long): List<MotoCliente>
}