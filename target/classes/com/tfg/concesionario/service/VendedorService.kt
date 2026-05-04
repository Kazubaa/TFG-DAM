package com.tfg.concesionario.service

import com.tfg.concesionario.model.Vendedor
import com.tfg.concesionario.repository.VendedorRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class VendedorService(private val repo: VendedorRepository) {
    fun getAll(): List<Vendedor> = repo.findAll()
    fun get(id: Long): Optional<Vendedor> = repo.findById(id)
    fun save(vendedor: Vendedor): Vendedor = repo.save(vendedor)
    fun delete(id: Long) = repo.deleteById(id)
    fun findByEmail(email: String): Vendedor? = repo.findByEmail(email)
}