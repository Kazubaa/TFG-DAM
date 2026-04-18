package com.tfg.concesionario.controller

import com.tfg.concesionario.dto.LoginRequest
import com.tfg.concesionario.dto.LoginResponse
import com.tfg.concesionario.dto.RegisterRequest
import com.tfg.concesionario.model.Rol
import com.tfg.concesionario.model.Usuario
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
    private val jwtService: JwtService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): Map<String, Any?> {

        val usuario = usuarioService.getByUsername(request.username)
            ?: throw RuntimeException("Usuario no encontrado")

        if (!passwordEncoder.matches(request.password, usuario.password)) {
            throw RuntimeException("Contraseña incorrecta")
        }

        // Generar JWT
        val token = jwtService.generateToken(
            username = usuario.username,
            rol = usuario.rol.name
        )

        // Devolver token + info básica
        return mapOf(
            "token" to token,
            "id" to usuario.id,
            "username" to usuario.username,
            "rol" to usuario.rol.name,
            "clienteId" to usuario.cliente?.id
        )
    }



    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): Map<String, Any?> {
        val usuario = Usuario(
            username = request.username,
            password = request.password,  // el service lo encripta
            rol = Rol.valueOf(request.rol)
        )
        val saved = usuarioService.save(usuario)
        return mapOf(
            "id" to saved.id,
            "username" to saved.username,
            "rol" to saved.rol.name,
            "clienteId" to saved.cliente?.id
        )
    }
}