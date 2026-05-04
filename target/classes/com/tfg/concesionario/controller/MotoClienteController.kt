package com.tfg.concesionario.controller

import com.tfg.concesionario.dto.MotoClienteRequest
import com.tfg.concesionario.model.MotoCliente
import com.tfg.concesionario.service.MotoClienteService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/motoCliente")
class MotoClienteController(private val service: MotoClienteService) {

    @GetMapping
    fun getAll(): List<MotoCliente> = service.getAll()

    @GetMapping("/{matricula}")
    fun get(@PathVariable matricula: String): MotoCliente = service.get(matricula)

    @GetMapping("/cliente/{clienteId}")
    fun getByCliente(@PathVariable clienteId: Long): List<MotoCliente> =
        service.getByCliente(clienteId)

    @PostMapping
    fun create(@RequestBody request: MotoClienteRequest): MotoCliente = service.save(request)

    @PutMapping("/{matricula}")
    fun update(@PathVariable matricula: String, @RequestBody request: MotoClienteRequest): MotoCliente =
        service.update(matricula, request)

    @DeleteMapping("/{matricula}")
    fun delete(@PathVariable matricula: String) = service.delete(matricula)
}