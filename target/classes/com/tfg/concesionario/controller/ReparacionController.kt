package com.tfg.concesionario.controller

import com.tfg.concesionario.dto.ReparacionRequest
import com.tfg.concesionario.model.Reparacion
import com.tfg.concesionario.service.ReparacionService
import com.tfg.concesionario.service.UsuarioService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
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
@RequestMapping("/reparaciones")
class ReparacionController(
    private val service: ReparacionService,
    private val usuarioService: UsuarioService
) {

    @GetMapping
    fun getAll(): List<Reparacion> {
        val usuario = getUsuarioActual()
        if (usuario.rol.name !in listOf("ADMIN", "MECANICO")) {
            throw RuntimeException("Sin permiso")
        }
        return service.getAll()
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Reparacion {
        val rep = service.get(id)
        val usuario = getUsuarioActual()
        if (usuario.rol.name == "CLIENTE" && usuario.cliente?.id != rep.cita.cliente.id) {
            throw RuntimeException("Sin permiso")
        }
        return rep
    }

    @GetMapping("/moto/{matricula}")
    fun getByMoto(@PathVariable matricula: String): List<Reparacion> =
        service.getByMotoMatricula(matricula)

    @GetMapping("/cliente/{clienteId}")
    fun getByCliente(@PathVariable clienteId: Long): List<Reparacion> {
        val usuario = getUsuarioActual()
        println("DEBUG getByCliente: username=${usuario.username}, rol=${usuario.rol.name}, clienteId_usuario=${usuario.cliente?.id}, clienteId_path=$clienteId")

        if (usuario.rol.name == "CLIENTE" && usuario.cliente?.id != clienteId) {
            throw RuntimeException("Sin permiso")
        }

        return service.getByCliente(clienteId)
    }

    @PostMapping
    fun create(@RequestBody request: ReparacionRequest): Reparacion = service.save(request)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ReparacionRequest): Reparacion =
        service.update(id, request)

    @PutMapping("/{id}/estado")
    fun actualizarEstado(@PathVariable id: Long, @RequestParam estado: String): Reparacion {
        val rep = service.get(id)
        val usuario = getUsuarioActual()

        if (usuario.rol.name == "CLIENTE") {
            if (usuario.cliente?.id != rep.cita.cliente.id) {
                throw RuntimeException("Sin permiso")
            }
            if (estado !in listOf("TALLER", "RECHAZADO", "COMPLETADO")) {
                throw RuntimeException("Acción no permitida")
            }
        }

        return service.actualizarEstado(id, estado)
    }

    @GetMapping("/mecanico/{mecanicoId}")
    fun getByMecanico(@PathVariable mecanicoId: Long): List<Reparacion> =
        service.getByMecanico(mecanicoId)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    private fun getUsuarioActual(): com.tfg.concesionario.model.Usuario {
        val auth = SecurityContextHolder.getContext().authentication ?: throw RuntimeException("No autenticado")
        val username = auth.name
        return usuarioService.getByUsername(username)
            ?: throw RuntimeException("Usuario no encontrado: $username")
    }
}