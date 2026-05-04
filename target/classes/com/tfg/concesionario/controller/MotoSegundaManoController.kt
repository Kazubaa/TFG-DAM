package com.tfg.concesionario.controller

import com.tfg.concesionario.dto.MotoSegundaManoRequest
import com.tfg.concesionario.model.MotoSegundaMano
import com.tfg.concesionario.service.MotoSegundaManoService
import org.springframework.security.access.prepost.PreAuthorize
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
@RequestMapping("/motosSegundaMano")
class MotoSegundaManoController(private val service: MotoSegundaManoService) {

    @GetMapping
    fun getAll() = service.getAll()

    @GetMapping("/filtrar")
    fun filtrar(
        @RequestParam(required = false) marca: String?,
        @RequestParam(required = false) modelo: String?,
        @RequestParam(required = false) cvMax: Int?,
        @RequestParam(required = false) cilindradaMax: Int?,
        @RequestParam(required = false) km: Int?,
        @RequestParam(required = false) precioMax: Double?,
        @RequestParam(required = false) matricula: String?
    ) = service.filtrar(marca, modelo, cvMax, km,cilindradaMax, precioMax, matricula)

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_VENDEDOR')")
    fun create(@RequestBody request: MotoSegundaManoRequest): MotoSegundaMano {
        val moto = MotoSegundaMano(
            marca = request.marca,
            modelo = request.modelo,
            precio = request.precio,
            cilindrada = request.cilindrada,
            km = request.km,
            cv = request.cv,
            matricula = request.matricula,
            imagenPrincipal = request.imagenPrincipal
        )
        return service.save(moto)
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_VENDEDOR')")
    fun update(@PathVariable id: Long, @RequestBody request: MotoSegundaManoRequest): MotoSegundaMano {
        val existing = service.get(id).orElseThrow { RuntimeException("Moto no encontrada") }
        return service.save(
            existing.copy(
                marca = request.marca,
                modelo = request.modelo,
                precio = request.precio,
                cilindrada = request.cilindrada,
                km = request.km,
                cv = request.cv,
                matricula = request.matricula,
                imagenPrincipal = request.imagenPrincipal ?: existing.imagenPrincipal
            )
        )
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_VENDEDOR')")
    fun delete(@PathVariable id: Long) = service.delete(id)
}