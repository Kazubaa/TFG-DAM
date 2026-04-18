package com.tfg.concesionario.service

import com.tfg.concesionario.dto.ReservaRequest
import com.tfg.concesionario.model.Reserva
import com.tfg.concesionario.repository.ClienteRepository
import com.tfg.concesionario.repository.MotoRepository
import com.tfg.concesionario.repository.MotoSegundaManoRepository
import com.tfg.concesionario.repository.ReservaRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ReservaService(
    private val repo: ReservaRepository,
    private val clienteRepo: ClienteRepository,
    private val motoRepo: MotoRepository,
    private val motoSegundaManoRepo: MotoSegundaManoRepository
) {

    fun getAll(): List<Reserva> = repo.findAll()

    fun get(id: Long): Optional<Reserva> = repo.findById(id)

    fun getByCliente(clienteId: Long): List<Reserva> = repo.findByClienteId(clienteId)

    fun save(request: ReservaRequest): Reserva {
        val cliente = clienteRepo.findById(request.clienteId)
            .orElseThrow { RuntimeException("Cliente no encontrado") }
        val moto = request.motoId?.let {
            motoRepo.findById(it).orElseThrow { RuntimeException("Moto no encontrada") }
        }
        val motoSegundaMano = request.motoSegundaManoId?.let {
            val m = motoSegundaManoRepo.findById(it)
                .orElseThrow { RuntimeException("Moto segunda mano no encontrada") }
            if (!m.disponible) throw RuntimeException("La moto no está disponible")
            m
        }

        // ⚠️ Ya NO se marca como no disponible al crear - solo al aceptar

        return repo.save(
            Reserva(
                cliente = cliente,
                moto = moto,
                motoSegundaMano = motoSegundaMano,
                fecha = request.fecha,
                hora = request.hora,
                estado = "PENDIENTE"
            )
        )
    }

    fun actualizarEstado(id: Long, estado: String): Reserva {
        val reserva = repo.findById(id).orElseThrow { RuntimeException("Reserva no encontrada") }

        when (estado) {
            "ACEPTADA" -> {
                // Al aceptar, marcar la moto como no disponible
                reserva.motoSegundaMano?.let {
                    motoSegundaManoRepo.save(it.copy(disponible = false))
                }
            }
            "RECHAZADA" -> {
                // Al rechazar, no hay que cambiar nada (la moto seguía disponible)
            }
            "CANCELADA" -> {
                // Si estaba aceptada y se cancela, devolver disponibilidad
                if (reserva.estado == "ACEPTADA") {
                    reserva.motoSegundaMano?.let {
                        motoSegundaManoRepo.save(it.copy(disponible = true))
                    }
                }
            }
        }

        return repo.save(reserva.copy(estado = estado))
    }

    fun update(id: Long, request: ReservaRequest): Reserva {
        val existing = repo.findById(id).orElseThrow { RuntimeException("Reserva no encontrada") }
        val cliente = clienteRepo.findById(request.clienteId)
            .orElseThrow { RuntimeException("Cliente no encontrado") }
        val moto = request.motoId?.let {
            motoRepo.findById(it).orElseThrow { RuntimeException("Moto no encontrada") }
        }
        val motoSegundaMano = request.motoSegundaManoId?.let {
            motoSegundaManoRepo.findById(it).orElseThrow { RuntimeException("Moto segunda mano no encontrada") }
        }

        return repo.save(
            existing.copy(
                cliente = cliente,
                moto = moto,
                motoSegundaMano = motoSegundaMano,
                fecha = request.fecha,
                hora = request.hora,
                estado = request.estado
            )
        )
    }

    fun delete(id: Long) = repo.deleteById(id)
}