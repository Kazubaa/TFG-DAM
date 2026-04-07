package com.tfg.concesionario.controller

import com.tfg.concesionario.dto.CitaRequest
import com.tfg.concesionario.model.Cita
import com.tfg.concesionario.service.CitaService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/citas")
class CitaController(private val service: CitaService) {

    @GetMapping
    fun getAll(): List<Cita> = service.getAll()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): Cita =
        service.get(id).orElseThrow { RuntimeException("Cita no encontrada") }

    @PostMapping
    fun create(@RequestBody request: CitaRequest): Cita = service.save(request)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CitaRequest): Cita =
        service.update(id, request)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}