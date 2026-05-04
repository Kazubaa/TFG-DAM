package com.tfg.concesionario.service

import com.tfg.concesionario.dto.MotoNuevaRequest
import com.tfg.concesionario.model.MotoNueva
import com.tfg.concesionario.repository.ImagenMotoNuevaRepository
import com.tfg.concesionario.repository.MotoNuevaRepository
import org.springframework.stereotype.Service

@Service
class MotoNuevaService(
    private val repo: MotoNuevaRepository,
    private val imgRepo: ImagenMotoNuevaRepository
) {
    fun getAll(): List<MotoNueva> = repo.findAll()

    fun get(id: Long): MotoNueva =
        repo.findById(id).orElseThrow { RuntimeException("Moto no encontrada") }

    fun getByMarca(marca: String): List<MotoNueva> = repo.findByMarca(marca)

    fun getByMarcaCategoria(marca: String, categoria: String): List<MotoNueva> =
        repo.findByMarcaAndCategoria(marca, categoria)

    fun save(request: MotoNuevaRequest): MotoNueva =
        repo.save(MotoNueva(
            marca = request.marca,
            modelo = request.modelo,
            categoria = request.categoria,
            anio = request.anio,
            precio = request.precio,
            cilindrada = request.cilindrada,
            cv = request.cv,
            peso = request.peso,
            descripcion = request.descripcion,
            videoFile = request.videoFile
        ))

    fun update(id: Long, request: MotoNuevaRequest): MotoNueva {
        val existing = get(id)
        return repo.save(existing.copy(
            marca = request.marca,
            modelo = request.modelo,
            categoria = request.categoria,
            anio = request.anio,
            precio = request.precio,
            cilindrada = request.cilindrada,
            cv = request.cv,
            peso = request.peso,
            descripcion = request.descripcion,
            videoFile = request.videoFile
        ))
    }

    fun delete(id: Long) {
        imgRepo.findByMotoNuevaIdOrderByOrdenAsc(id).forEach { imgRepo.deleteById(it.id) }
        repo.deleteById(id)
    }
}