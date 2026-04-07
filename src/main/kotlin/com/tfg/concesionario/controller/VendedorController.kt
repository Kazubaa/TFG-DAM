package com.tfg.concesionario.controller

import com.tfg.concesionario.model.Vendedor
import com.tfg.concesionario.service.VendedorService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/vendedores")
class VendedorController(private val service: VendedorService) {

    @GetMapping
    fun getAll() = service.getAll()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long) = service.get(id)

    @PostMapping
    fun create(@RequestBody vendedor: Vendedor) = service.save(vendedor)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody vendedor: Vendedor): Vendedor {
        val existing = service.get(id).orElseThrow { RuntimeException("Vendedor no encontrado") }
        return service.save(
            existing.copy(
                nombre = vendedor.nombre,
                email = vendedor.email,
                telefono = vendedor.telefono
            )
        )
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}