package com.tfg.concesionario.controller

import com.tfg.concesionario.service.VideoMotoNuevaService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/videosMotoNueva")
class VideoMotoNuevaController(private val service: VideoMotoNuevaService) {

    @PostMapping("/upload")
    fun subir(
        @RequestParam("motoId") motoId: Long,
        @RequestParam("file") file: MultipartFile
    ): Map<String, String> {
        val ruta = service.subir(motoId, file)
        return mapOf("url" to ruta)
    }

    @DeleteMapping("/{motoId}")
    fun eliminar(@PathVariable motoId: Long) = service.eliminar(motoId)
}