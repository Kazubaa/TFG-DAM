package com.tfg.concesionario.controller

import com.tfg.concesionario.dto.VentaDTO
import com.tfg.concesionario.model.Venta
import com.tfg.concesionario.service.VentaService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ventas")
class VentaController(private val service: VentaService) {

    @GetMapping
    fun getAll(): List<Venta> = service.getAll()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): Venta = service.get(id).orElseThrow { RuntimeException("Venta no encontrada") }

    @PostMapping
    fun create(@RequestBody dto: VentaDTO): Venta = service.save(dto)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: VentaDTO): Venta = service.update(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}