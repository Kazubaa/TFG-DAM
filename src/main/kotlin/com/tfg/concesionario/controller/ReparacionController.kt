package com.tfg.concesionario.controller

import com.tfg.concesionario.dto.ReparacionRequest
import com.tfg.concesionario.model.Reparacion
import com.tfg.concesionario.service.ReparacionService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/reparaciones")
class ReparacionController(private val service: ReparacionService) {

    @GetMapping
    fun getAll(): List<Reparacion> = service.getAll()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): Reparacion =
        service.get(id).orElseThrow { RuntimeException("Reparación no encontrada") }

    @PostMapping
    fun create(@RequestBody request: ReparacionRequest): Reparacion = service.save(request)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ReparacionRequest): Reparacion =
        service.update(id, request)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @GetMapping("/cita/{citaId}")
    fun getByCita(@PathVariable citaId: Long): List<Reparacion> = service.getByCita(citaId)

    @GetMapping("/mecanico/{mecanicoId}")
    fun getByMecanico(@PathVariable mecanicoId: Long): List<Reparacion> = service.getByMecanico(mecanicoId)
}