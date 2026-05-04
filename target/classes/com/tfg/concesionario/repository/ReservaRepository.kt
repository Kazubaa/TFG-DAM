package com.tfg.concesionario.repository

import com.tfg.concesionario.model.Reserva
import org.springframework.data.jpa.repository.JpaRepository

interface ReservaRepository : JpaRepository<Reserva, Long>{
    fun findByClienteId(clienteId: Long): List<Reserva>
    fun findByMotoSegundaManoIdAndEstadoIn(motoId: Long, estados: List<String>): List<Reserva>
}

