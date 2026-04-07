package com.tfg.concesionario.controller

import com.tfg.concesionario.model.Cliente
import com.tfg.concesionario.service.ClienteService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/clientes")
class ClienteController(private val service: ClienteService) {

    @GetMapping
    fun getAll(): List<Cliente> = service.getAll()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): Cliente = service.get(id).orElseThrow { RuntimeException("Cliente no encontrado") }

    @PostMapping
    fun create(@RequestBody cliente: Cliente): Cliente = service.save(cliente)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody cliente: Cliente): Cliente = service.update(id, cliente)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}