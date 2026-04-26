package com.tfg.concesionario.service

import com.tfg.concesionario.dto.CitaRequest
import com.tfg.concesionario.model.Cita
import com.tfg.concesionario.model.Mecanico
import com.tfg.concesionario.repository.CitaRepository
import com.tfg.concesionario.repository.ClienteRepository
import com.tfg.concesionario.repository.MecanicoRepository
import com.tfg.concesionario.repository.MotoClienteRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.util.Optional
@Service
class CitaService(
    private val repo: CitaRepository,
    private val clienteRepo: ClienteRepository,
    private val mecanicoRepo: MecanicoRepository,
    private val motoClienteRepo: MotoClienteRepository
) {
    fun getAll(): List<Cita> = repo.findAll()
    fun get(id: Long): Optional<Cita> = repo.findById(id)
    fun getByCliente(clienteId: Long): List<Cita> = repo.findByClienteId(clienteId)
    fun getByMecanico(mecanicoId: Long): List<Cita> = repo.findByMecanicoId(mecanicoId)

    fun save(request: CitaRequest): Cita {
        val cliente = clienteRepo.findById(request.clienteId)
            .orElseThrow { RuntimeException("Cliente no encontrado") }
        val motoCliente = request.motoMatricula?.let {
            motoClienteRepo.findById(it).orElseThrow { RuntimeException("Moto no encontrada") }
        }

        // Verificar que hay mecánicos disponibles en esa franja
        if (!hayMecanicosDisponibles(request.fecha, request.hora)) {
            throw RuntimeException("No hay mecánicos disponibles en esa franja horaria")
        }

        return repo.save(
            Cita(
                cliente = cliente,
                motoCliente = motoCliente,
                fecha = request.fecha,
                hora = request.hora,
                tipo = request.tipo,
                descripcion = request.descripcion,
                estado = "PENDIENTE"
            )
        )
    }

    fun update(id: Long, request: CitaRequest): Cita {
        val existing = repo.findById(id)
            .orElseThrow { RuntimeException("Cita no encontrada") }

        if (existing.estado != "PENDIENTE") {
            throw RuntimeException("Solo se pueden modificar citas pendientes")
        }

        // Si cambia fecha u hora, verificar disponibilidad
        if (existing.fecha != request.fecha || existing.hora != request.hora) {
            if (!hayMecanicosDisponibles(request.fecha, request.hora, excluirCitaId = id)) {
                throw RuntimeException("No hay mecánicos disponibles en esa franja")
            }
        }

        return repo.save(existing.copy(
            fecha = request.fecha,
            hora = request.hora,
            tipo = request.tipo,
            descripcion = request.descripcion
        ))
    }

    fun actualizarEstado(id: Long, estado: String, mecanicoId: Long? = null): Cita {
        val cita = repo.findById(id).orElseThrow { RuntimeException("Cita no encontrada") }

        val mecanico = if (estado == "ACEPTADA" && cita.mecanico == null) {
            mecanicoId?.let {
                mecanicoRepo.findById(it).orElseThrow { RuntimeException("Mecánico no encontrado") }
            } ?: cita.mecanico
        } else cita.mecanico

        return repo.save(cita.copy(
            mecanico = mecanico,
            estado = estado,
            descripcion = cita.descripcion
        ))
    }

    fun cancelar(id: Long): Cita = actualizarEstado(id, "CANCELADA")

    fun delete(id: Long) = repo.deleteById(id)

    
    fun getMecanicosDisponibles(fecha: LocalDate, hora: LocalTime): List<Long> {
        val todos = mecanicoRepo.findAll().map { it.id }
        val ocupadosIds = repo.findByFechaAndHora(fecha, hora)
            .filter { it.estado == "ACEPTADA" }
            .mapNotNull { it.mecanico?.id }
        return todos - ocupadosIds.toSet()
    }

    private fun hayMecanicosDisponibles(
        fecha: LocalDate,
        hora: LocalTime,
        excluirCitaId: Long? = null
    ): Boolean {
        val totalMecanicos = mecanicoRepo.count()
        if (totalMecanicos == 0L) return false

        val citasEnFranja = repo.findByFechaAndHora(fecha, hora)
            .filter { it.estado == "ACEPTADA" && it.id != excluirCitaId }

        return citasEnFranja.size < totalMecanicos
    }
}