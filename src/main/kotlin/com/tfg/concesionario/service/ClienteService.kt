package com.tfg.concesionario.service


import com.tfg.concesionario.dto.ClienteRequest
import com.tfg.concesionario.model.Cliente
import com.tfg.concesionario.repository.ClienteRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ClienteService(private val repo: ClienteRepository) {
    fun getAll(): List<Cliente> = repo.findAll()

    fun get(id: Long): Optional<Cliente> = repo.findById(id)

    fun save(cliente: Cliente): Cliente = repo.save(cliente)

    fun update(id: Long, request: ClienteRequest): Cliente {
        val existing = repo.findById(id).orElseThrow { RuntimeException("Cliente no encontrado") }
        return repo.save(existing.copy(
            nombre = request.nombre,
            email = request.email,
            telefono = request.telefono
        ))
    }

    fun delete(id: Long) = repo.deleteById(id)
}