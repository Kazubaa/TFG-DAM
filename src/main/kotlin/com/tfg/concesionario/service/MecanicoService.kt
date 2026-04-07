package com.tfg.concesionario.service

import com.tfg.concesionario.model.Mecanico
import com.tfg.concesionario.repository.MecanicoRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class MecanicoService(private val repo: MecanicoRepository) {
    fun getAll(): List<Mecanico> = repo.findAll()
    fun get(id: Long): Optional<Mecanico> = repo.findById(id)
    fun save(mecanico: Mecanico): Mecanico = repo.save(mecanico)
    fun delete(id: Long) = repo.deleteById(id)
    fun findByEmail(email: String): Mecanico? = repo.findByEmail(email)
}