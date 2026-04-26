package com.tfg.concesionario.service

import com.tfg.concesionario.model.Cliente
import com.tfg.concesionario.model.Mecanico
import com.tfg.concesionario.model.Rol
import com.tfg.concesionario.model.Usuario
import com.tfg.concesionario.model.Vendedor
import com.tfg.concesionario.repository.ClienteRepository
import com.tfg.concesionario.repository.MecanicoRepository
import com.tfg.concesionario.repository.UsuarioRepository
import com.tfg.concesionario.repository.VendedorRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UsuarioService(
    private val repo: UsuarioRepository,
    private val clienteRepo: ClienteRepository,
    private val mecanicoRepo: MecanicoRepository,
    private val vendedorRepo: VendedorRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun getAll(): List<Usuario> = repo.findAll()

    fun get(id: Long): Optional<Usuario> = repo.findById(id)

    fun getByUsername(username: String): Usuario? = repo.findByUsername(username)


    fun save(usuario: Usuario): Usuario {
        val rawPassword = usuario.password.takeIf { it.isNotBlank() }
            ?: throw RuntimeException("Contraseña requerida")

        val encryptedPassword: String = passwordEncoder.encode(rawPassword)
            ?: throw RuntimeException("Error al encriptar la contraseña")

        val clienteAsociado = if (usuario.rol == Rol.CLIENTE && usuario.cliente == null) {
            clienteRepo.save(Cliente(
                nombre = usuario.username,
                email = "${usuario.username}@cliente.com",
                telefono = "000000000"
            ))
        } else usuario.cliente

        // Crear Mecanico automáticamente
        if (usuario.rol == Rol.MECANICO) {
            mecanicoRepo.save(
                Mecanico(
                    nombre = usuario.username,
                    email = "${usuario.username}@taller.com",
                    telefono = "000000000"
                )
            )
        }

        // Crear Vendedor automáticamente
        if (usuario.rol == Rol.VENDEDOR) {
            vendedorRepo.save(
                Vendedor(
                    nombre = usuario.username,
                    email = "${usuario.username}@concesionario.com",
                    telefono = "000000000"
                )
            )
        }

        return repo.save(usuario.copy(
            password = encryptedPassword,
            cliente = clienteAsociado
        ))
    }

    fun update(id: Long, usuario: Usuario): Usuario {
        val existing = repo.findById(id).orElseThrow { RuntimeException("Usuario no encontrado") }
        val password = usuario.password?.takeIf { it.isNotBlank() }?.let {
            passwordEncoder.encode(it)
        } ?: existing.password

        return repo.save(existing.copy(
            username = usuario.username,
            password = password,
            rol = usuario.rol,
            cliente = usuario.cliente
        ))
    }

    fun delete(id: Long) = repo.deleteById(id)
}