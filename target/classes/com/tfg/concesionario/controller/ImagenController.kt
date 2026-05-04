package com.tfg.concesionario.controller

import com.tfg.concesionario.model.Imagen
import com.tfg.concesionario.service.ImagenService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/imagenes")
class ImagenController(private val service: ImagenService) {

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_VENDEDOR')")
    fun upload(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("motoId") motoId: Long
    ): Imagen {
        return service.subirImagen(file, motoId)
    }

    @GetMapping("/moto/{motoId}")
    fun getByMoto(@PathVariable motoId: Long): List<Imagen> {
        return service.getImagenes(motoId)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_VENDEDOR')")
    fun delete(@PathVariable id: Long) {
        service.eliminarImagen(id)
    }

    @PutMapping("/principal")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_VENDEDOR')")
    fun setPrincipal(
        @RequestParam motoId: Long,
        @RequestParam url: String
    ) {
        service.setPrincipal(motoId, url)
    }
}