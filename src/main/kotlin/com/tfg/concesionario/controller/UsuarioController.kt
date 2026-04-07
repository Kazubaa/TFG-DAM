package com.tfg.concesionario.controller

import com.tfg.concesionario.dto.UsuarioDTO
import com.tfg.concesionario.model.Usuario
import com.tfg.concesionario.service.UsuarioService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/usuarios")
class UsuarioController(private val service: UsuarioService) {

    private fun Usuario.toDTO() = UsuarioDTO(
        id = this.id,
        username = this.username,
        rol = this.rol,
        clienteId = this.cliente?.id
    )

    @GetMapping
    fun getAll(): List<Usuario> = service.getAll()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): Usuario = service.get(id).orElseThrow { RuntimeException("Usuario no encontrado") }

    @PostMapping
    fun create(@RequestBody usuario: Usuario): Usuario {
        return service.save(usuario)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody usuario: Usuario): Usuario = service.update(id, usuario)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)


}