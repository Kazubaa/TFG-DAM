package com.tfg.concesionario.controller

import com.tfg.concesionario.dto.LoginRequest
import com.tfg.concesionario.dto.LoginResponse
import com.tfg.concesionario.dto.RegisterRequest
import com.tfg.concesionario.model.Rol
import com.tfg.concesionario.model.Usuario
import com.tfg.concesionario.repository.MecanicoRepository
import com.tfg.concesionario.repository.VendedorRepository
import com.tfg.concesionario.security.JwtService
import com.tfg.concesionario.service.UsuarioService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val usuarioService: UsuarioService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val mecanicoRepo: MecanicoRepository,
    private val vendedorRepo: VendedorRepository
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): Map<String, Any?> {
        val usuario = usuarioService.getByUsername(request.username)
            ?: throw RuntimeException("Usuario no encontrado")

        if (!passwordEncoder.matches(request.password, usuario.password)) {
            throw RuntimeException("Contraseña incorrecta")
        }

        val token = jwtService.generateToken(
            username = usuario.username,
            rol = usuario.rol.name
        )

        val mecanicoId = if (usuario.rol == Rol.MECANICO) {
            mecanicoRepo.findAll().find { it.nombre == usuario.username }?.id
        } else null

        val vendedorId = if (usuario.rol == Rol.VENDEDOR) {
            vendedorRepo.findAll().find { it.nombre == usuario.username }?.id
        } else null

        return mapOf(
            "token" to token,
            "id" to usuario.id,
            "username" to usuario.username,
            "rol" to usuario.rol.name,
            "clienteId" to usuario.cliente?.id,
            "mecanicoId" to mecanicoId,
            "vendedorId" to vendedorId
        )
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): Map<String, Any?> {
        val usuario = Usuario(
            username = request.username,
            password = request.password,
            rol = Rol.valueOf(request.rol)
        )
        val saved = usuarioService.save(usuario)

        val mecanicoId = if (saved.rol == Rol.MECANICO) {
            mecanicoRepo.findAll().find { it.nombre == saved.username }?.id
        } else null

        val vendedorId = if (saved.rol == Rol.VENDEDOR) {
            vendedorRepo.findAll().find { it.nombre == saved.username }?.id
        } else null

        return mapOf(
            "id" to saved.id,
            "username" to saved.username,
            "rol" to saved.rol.name,
            "clienteId" to saved.cliente?.id,
            "mecanicoId" to mecanicoId,
            "vendedorId" to vendedorId
        )
    }
}