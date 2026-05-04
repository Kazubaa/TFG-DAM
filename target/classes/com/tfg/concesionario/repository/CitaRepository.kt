package com.tfg.concesionario.repository

import com.tfg.concesionario.model.Cita
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.time.LocalTime

interface CitaRepository : JpaRepository<Cita, Long> {
    fun findByFechaAndHora(fecha: LocalDate, hora: LocalTime): List<Cita>
    fun findByMecanicoIdAndFechaAndHora(mecanicoId: Long, fecha: LocalDate, hora: LocalTime): List<Cita>

    fun findByClienteId(clienteId: Long): List<Cita>
    fun findByMecanicoId(mecanicoId: Long): List<Cita>
    fun findByMotoClienteMatricula(matricula: String): List<Cita>


}