package com.tfg.concesionario.controller

import com.tfg.concesionario.dto.MotoNuevaRequest
import com.tfg.concesionario.model.MotoNueva
import com.tfg.concesionario.service.MotoNuevaService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/motosNuevas")
class MotoNuevaController(private val service: MotoNuevaService) {

    @GetMapping
    fun getAll(): List<MotoNueva> = service.getAll()

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): MotoNueva = service.get(id)

    @GetMapping("/marca/{marca}")
    fun getByMarca(@PathVariable marca: String): List<MotoNueva> = service.getByMarca(marca)

    @GetMapping("/marca/{marca}/categoria/{categoria}")
    fun getByMarcaCategoria(
        @PathVariable marca: String,
        @PathVariable categoria: String
    ): List<MotoNueva> = service.getByMarcaCategoria(marca, categoria)

    @PostMapping
    fun create(@RequestBody request: MotoNuevaRequest): MotoNueva = service.save(request)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: MotoNuevaRequest): MotoNueva =
        service.update(id, request)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}