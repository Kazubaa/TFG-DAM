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

        motoSegundaMano?.let { motoSegundaManoRepo.save(it.copy(disponible = false)) }

        return repo.save(
            Reserva(
                cliente = cliente,
                moto = moto,
                motoSegundaMano = motoSegundaMano,
                fecha = request.fecha,
                estado = request.estado
            )
        )
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

        if (request.estado == "CANCELADA" || request.estado == "RECHAZADA") {
            existing.motoSegundaMano?.let { motoSegundaManoRepo.save(it.copy(disponible = true)) }
        }

        return repo.save(
            existing.copy(
                cliente = cliente,
                moto = moto,
                motoSegundaMano = motoSegundaMano,
                fecha = request.fecha,
                estado = request.estado
            )
        )
    }

    fun delete(id: Long) = repo.deleteById(id)
}