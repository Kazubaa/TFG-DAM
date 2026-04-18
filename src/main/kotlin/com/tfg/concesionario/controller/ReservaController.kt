package com.tfg.concesionario.controller

import com.tfg.concesionario.dto.ReservaRequest
import com.tfg.concesionario.model.Reserva
import com.tfg.concesionario.service.ReservaService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reservas")
class ReservaController(private val service: ReservaService) {

    @GetMapping
    fun getAll(): List<Reserva> = service.getAll()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): Reserva =
        service.get(id).orElseThrow { RuntimeException("Reserva no encontrada") }

    @PostMapping
    fun create(@RequestBody request: ReservaRequest): Reserva = service.save(request)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ReservaRequest): Reserva =
        service.update(id, request)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @GetMapping("/cliente/{clienteId}")
    fun getByCliente(@PathVariable clienteId: Long): List<Reserva> = service.getByCliente(clienteId)

    @PutMapping("/{id}/estado")
    fun actualizarEstado(@PathVariable id: Long, @RequestParam estado: String): Reserva =
        service.actualizarEstado(id, estado)
}