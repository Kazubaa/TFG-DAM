package com.tfg.concesionario.controller

import com.tfg.concesionario.model.ImagenMotoNueva
import com.tfg.concesionario.service.ImagenMotoNuevaService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/imagenesMotoNueva")
class ImagenMotoNuevaController(private val service: ImagenMotoNuevaService) {

    @GetMapping("/moto/{motoId}")
    fun getByMoto(@PathVariable motoId: Long): List<ImagenMotoNueva> =
        service.getByMoto(motoId)

    @GetMapping("/moto/{motoId}/tipo/{tipo}")
    fun getByMotoYTipo(
        @PathVariable motoId: Long,
        @PathVariable tipo: String
    ): List<ImagenMotoNueva> = service.getByMotoYTipo(motoId, tipo)

    @PostMapping("/upload")
    fun subir(
        @RequestParam("motoId") motoId: Long,
        @RequestParam("file") file: MultipartFile,
        @RequestParam("tipo") tipo: String
    ): ImagenMotoNueva = service.subir(motoId, file, tipo)

    @DeleteMapping("/{id}")
    fun eliminar(@PathVariable id: Long) = service.eliminar(id)
}