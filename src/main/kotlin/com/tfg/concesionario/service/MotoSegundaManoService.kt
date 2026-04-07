package com.tfg.concesionario.service

import com.tfg.concesionario.model.MotoSegundaMano
import com.tfg.concesionario.repository.MotoSegundaManoRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class MotoSegundaManoService(private val repo: MotoSegundaManoRepository) {

    fun getAll(): List<MotoSegundaMano> = repo.findAll()
    fun get(id: Long): Optional<MotoSegundaMano> = repo.findById(id)
    fun save(moto: MotoSegundaMano): MotoSegundaMano = repo.save(moto)
    fun delete(id: Long) = repo.deleteById(id)

    fun filtrar(
        marca: String?,
        modelo: String?,
        cvMax: Int?,
        cilindradaMax: Int?,
        precioMax: Double?,
        matricula: String?
    ): List<MotoSegundaMano> = repo.filtrar(marca, modelo, cvMax, cilindradaMax, precioMax, matricula)

    fun getMarcas(): List<String> = repo.findDistinctMarcas()
    fun findByMarcaAndModelo(marca: String, modelo: String): List<MotoSegundaMano> =
        repo.findByMarcaAndModelo(marca, modelo)

    fun getById(id: Long): MotoSegundaMano {
        return repo.findById(id)
            .orElseThrow { RuntimeException("Moto no encontrada") }
    }
}