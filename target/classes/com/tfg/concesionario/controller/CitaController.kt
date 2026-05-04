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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalTime


@RestController
@RequestMapping("/citas")
class CitaController(private val service: CitaService) {

    @GetMapping fun getAll(): List<Cita> = service.getAll()

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Cita =
        service.get(id).orElseThrow { RuntimeException("Cita no encontrada") }

    @GetMapping("/cliente/{clienteId}")
    fun getByCliente(@PathVariable clienteId: Long): List<Cita> = service.getByCliente(clienteId)

    @GetMapping("/mecanico/{mecanicoId}")
    fun getByMecanico(@PathVariable mecanicoId: Long): List<Cita> = service.getByMecanico(mecanicoId)

    @GetMapping("/disponibilidad")
    fun getMecanicosDisponibles(
        @RequestParam fecha: LocalDate,
        @RequestParam hora: LocalTime
    ): List<Long> = service.getMecanicosDisponibles(fecha, hora)

    @PostMapping
    fun create(@RequestBody request: CitaRequest): Cita = service.save(request)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CitaRequest): Cita =
        service.update(id, request)

    @PutMapping("/{id}/estado")
    fun actualizarEstado(
        @PathVariable id: Long,
        @RequestParam estado: String,
        @RequestParam(required = false) mecanicoId: Long?
    ): Cita = service.actualizarEstado(id, estado, mecanicoId)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}