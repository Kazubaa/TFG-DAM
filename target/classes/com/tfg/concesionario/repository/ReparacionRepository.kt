package com.tfg.concesionario.repository

import com.tfg.concesionario.model.Reparacion
import org.springframework.data.jpa.repository.JpaRepository

interface ReparacionRepository : JpaRepository<Reparacion, Long> {
    fun findByCitaId(citaId: Long): List<Reparacion>
    fun findByMecanicoId(mecanicoId: Long): List<Reparacion>

    fun findByCitaMotoClienteMatricula(matricula: String): List<Reparacion>
    fun findByCitaClienteId(clienteId: Long): List<Reparacion>
}