package com.tfg.concesionario.service

import com.tfg.concesionario.dto.MotoClienteRequest
import com.tfg.concesionario.model.MotoCliente
import com.tfg.concesionario.repository.ClienteRepository
import com.tfg.concesionario.repository.MotoClienteRepository
import org.springframework.stereotype.Service

@Service
class MotoClienteService(
    private val repo: MotoClienteRepository,
    private val clienteRepo: ClienteRepository
) {
    fun getAll(): List<MotoCliente> = repo.findAll()

    fun getByCliente(clienteId: Long): List<MotoCliente> =
        repo.findByClienteId(clienteId)

    fun get(matricula: String): MotoCliente =
        repo.findById(matricula).orElseThrow { RuntimeException("Moto no encontrada") }

    fun save(request: MotoClienteRequest): MotoCliente {
        val cliente = clienteRepo.findById(request.clienteId)
            .orElseThrow { RuntimeException("Cliente no encontrado") }

        if (repo.existsById(request.matricula))
            throw RuntimeException("Ya existe una moto con esa matrícula")

        return repo.save(
            MotoCliente(
                matricula = request.matricula,
                marca = request.marca,
                modelo = request.modelo,
                km = request.km,
                cliente = cliente
            )
        )
    }

    fun update(matricula: String, request: MotoClienteRequest): MotoCliente {
        val existing = repo.findById(matricula)
            .orElseThrow { RuntimeException("Moto no encontrada") }

        return repo.save(existing.copy(
            marca = request.marca,
            modelo = request.modelo,
            km = request.km
        ))
    }

    fun delete(matricula: String) = repo.deleteById(matricula)
}