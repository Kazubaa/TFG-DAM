package com.tfg.concesionario.service

import com.tfg.concesionario.dto.CitaRequest
import com.tfg.concesionario.model.Cita
import com.tfg.concesionario.model.Mecanico
import com.tfg.concesionario.repository.CitaRepository
import com.tfg.concesionario.repository.ClienteRepository
import com.tfg.concesionario.repository.MecanicoRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class CitaService(
    private val repo: CitaRepository,
    private val clienteRepo: ClienteRepository,
    private val mecanicoRepo: MecanicoRepository
) {

    fun getAll(): List<Cita> = repo.findAll()

    fun get(id: Long): Optional<Cita> = repo.findById(id)

    fun save(request: CitaRequest): Cita {
        val cliente = clienteRepo.findById(request.clienteId)
            .orElseThrow { RuntimeException("Cliente no encontrado") }
        val mecanico = request.mecanicoId?.let {
            mecanicoRepo.findById(it).orElseThrow { RuntimeException("Mecánico no encontrado") }
        }
        mecanico?.let {
            val conflictos = repo.findByMecanicoIdAndFechaAndHora(it.id, request.fecha, request.hora)
            if (conflictos.isNotEmpty()) {
                throw RuntimeException("Ya existe una cita para ese mecánico a esa hora")
            }
        }
        return repo.save(Cita(
            cliente = cliente,
            mecanico = mecanico,
            fecha = request.fecha,
            hora = request.hora,
            estado = request.estado
        ))
    }

    fun update(id: Long, request: CitaRequest): Cita {
        val existing = repo.findById(id).orElseThrow { RuntimeException("Cita no encontrada") }
        val cliente = clienteRepo.findById(request.clienteId)
            .orElseThrow { RuntimeException("Cliente no encontrado") }
        val mecanico = request.mecanicoId?.let {
            mecanicoRepo.findById(it).orElseThrow { RuntimeException("Mecánico no encontrado") }
        }
        mecanico?.let {
            val conflictos = repo.findByMecanicoIdAndFechaAndHora(it.id, request.fecha, request.hora)
                .filter { c -> c.id != id }
            if (conflictos.isNotEmpty()) {
                throw RuntimeException("Ya existe una cita para ese mecánico a esa hora")
            }
        }
        return repo.save(existing.copy(
            cliente = cliente,
            mecanico = mecanico,
            fecha = request.fecha,
            hora = request.hora,
            estado = request.estado
        ))
    }

    fun delete(id: Long) = repo.deleteById(id)
}