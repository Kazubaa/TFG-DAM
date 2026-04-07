package com.tfg.concesionario.service

import com.tfg.concesionario.dto.ReparacionRequest
import com.tfg.concesionario.model.Reparacion
import com.tfg.concesionario.repository.CitaRepository
import com.tfg.concesionario.repository.MecanicoRepository
import com.tfg.concesionario.repository.ReparacionRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ReparacionService(
    private val repo: ReparacionRepository,
    private val citaRepo: CitaRepository,
    private val mecanicoRepo: MecanicoRepository
) {

    fun getAll(): List<Reparacion> = repo.findAll()

    fun get(id: Long): Optional<Reparacion> = repo.findById(id)

    fun save(request: ReparacionRequest): Reparacion {
        val cita = citaRepo.findById(request.citaId)
            .orElseThrow { RuntimeException("Cita no encontrada") }
        val mecanico = mecanicoRepo.findById(request.mecanicoId)
            .orElseThrow { RuntimeException("Mecánico no encontrado") }
        return repo.save(Reparacion(
            cita = cita,
            mecanico = mecanico,
            descripcion = request.descripcion,
            fecha = request.fecha,
            estado = request.estado
        ))
    }

    fun update(id: Long, request: ReparacionRequest): Reparacion {
        val existing = repo.findById(id).orElseThrow { RuntimeException("Reparación no encontrada") }
        val cita = citaRepo.findById(request.citaId)
            .orElseThrow { RuntimeException("Cita no encontrada") }
        val mecanico = mecanicoRepo.findById(request.mecanicoId)
            .orElseThrow { RuntimeException("Mecánico no encontrado") }
        return repo.save(existing.copy(
            cita = cita,
            mecanico = mecanico,
            descripcion = request.descripcion,
            fecha = request.fecha,
            estado = request.estado
        ))
    }

    fun delete(id: Long) = repo.deleteById(id)

    fun getByCita(citaId: Long): List<Reparacion> = repo.findByCitaId(citaId)

    fun getByMecanico(mecanicoId: Long): List<Reparacion> = repo.findByMecanicoId(mecanicoId)
}