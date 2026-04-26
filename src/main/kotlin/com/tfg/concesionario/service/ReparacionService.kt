package com.tfg.concesionario.service

import com.tfg.concesionario.dto.ReparacionRequest
import com.tfg.concesionario.model.ItemReparacion
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

    fun get(id: Long): Reparacion =
        repo.findById(id).orElseThrow { RuntimeException("Reparación no encontrada") }

    fun getByMotoMatricula(matricula: String): List<Reparacion> =
        repo.findByCitaMotoClienteMatricula(matricula)

    fun getByCliente(clienteId: Long): List<Reparacion> =
        repo.findByCitaClienteId(clienteId)

    fun save(request: ReparacionRequest): Reparacion {
        val cita = citaRepo.findById(request.citaId)
            .orElseThrow { RuntimeException("Cita no encontrada") }
        val mecanico = mecanicoRepo.findById(request.mecanicoId)
            .orElseThrow { RuntimeException("Mecánico no encontrado") }

        val reparacion = Reparacion(
            cita = cita,
            mecanico = mecanico,
            descripcion = request.descripcion,
            estado = request.estado
        )

        val items = request.items.map {
            ItemReparacion(
                descripcion = it.descripcion,
                tipo = it.tipo,
                cantidad = it.cantidad,
                precioUnitario = it.precioUnitario,
                reparacion = reparacion
            )
        }.toMutableList()

        reparacion.items.addAll(items)

        // Calcular totales
        val subtotal = items.sumOf { it.cantidad * it.precioUnitario }
        val iva = subtotal * 0.21
        reparacion.subtotal = subtotal
        reparacion.iva = iva
        reparacion.total = subtotal + iva

        return repo.save(reparacion)
    }

    fun update(id: Long, request: ReparacionRequest): Reparacion {
        val existing = repo.findById(id)
            .orElseThrow { RuntimeException("Reparación no encontrada") }

        existing.items.clear()

        val newItems = request.items.map {
            ItemReparacion(
                descripcion = it.descripcion,
                tipo = it.tipo,
                cantidad = it.cantidad,
                precioUnitario = it.precioUnitario,
                reparacion = existing
            )
        }
        existing.items.addAll(newItems)

        val subtotal = newItems.sumOf { it.cantidad * it.precioUnitario }
        existing.subtotal = subtotal
        existing.iva = subtotal * 0.21
        existing.total = subtotal + existing.iva
        existing.estado = request.estado

        return repo.save(existing.copy(descripcion = request.descripcion))
    }

    fun actualizarEstado(id: Long, estado: String): Reparacion {
        val rep = repo.findById(id).orElseThrow { RuntimeException("Reparación no encontrada") }
        rep.estado = estado

        // Si la reparación se completa, marca tambien la cita como COMPLETADA
        if (estado == "COMPLETADO") {
            val cita = rep.cita.copy(estado = "COMPLETADA")
            citaRepo.save(cita)
        }

        return repo.save(rep)
    }

    fun getByMecanico(mecanicoId: Long): List<Reparacion> =
        repo.findByMecanicoId(mecanicoId)

    fun delete(id: Long) = repo.deleteById(id)
}