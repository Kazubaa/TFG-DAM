package com.tfg.concesionario.controller

import com.tfg.concesionario.model.Mecanico
import com.tfg.concesionario.service.MecanicoService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mecanicos")
class MecanicoController(private val service: MecanicoService) {

    @GetMapping
    fun getAll() = service.getAll()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long) = service.get(id)

    @PostMapping
    fun create(@RequestBody mecanico: Mecanico) = service.save(mecanico)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody mecanico: Mecanico): Mecanico {
        val existing = service.get(id).orElseThrow { RuntimeException("Mecánico no encontrado") }
        return service.save(
            existing.copy(
                nombre = mecanico.nombre,
                email = mecanico.email,
                telefono = mecanico.telefono
            )
        )
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}