package com.tfg.concesionario.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
@RequestMapping("/imagenes")
class PromocionController {

    @GetMapping("/promociones")
    fun getMotos(): List<String> {
        val dir = File("/app/uploads/imagenes/promociones")
        if (!dir.exists()) return emptyList()
        return dir.listFiles()
            ?.filter { it.isFile }
            ?.map { "imagenes/promociones/${it.name}" }
            ?: emptyList()
    }

    @GetMapping("/promomecanico")
    fun getMecanico(): List<String> {
        val dir = File("/app/uploads/imagenes/promomecanico")
        if (!dir.exists()) return emptyList()
        return dir.listFiles()
            ?.filter { it.isFile }
            ?.map { "imagenes/promomecanico/${it.name}" }
            ?: emptyList()
    }


    @GetMapping("/promociones/{filename}", produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE])
    fun getPromocionImage(@PathVariable filename: String): ByteArray {
        val file = File("/app/uploads/imagenes/promociones/$filename")
        if (!file.exists()) throw RuntimeException("Imagen no encontrada")
        return file.readBytes()
    }

    @GetMapping("/promomecanico/{filename}", produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE])
    fun getPromomecanicoImage(@PathVariable filename: String): ByteArray {
        val file = File("/app/uploads/imagenes/promomecanico/$filename")
        if (!file.exists()) throw RuntimeException("Imagen no encontrada")
        return file.readBytes()
    }

    @GetMapping("/motosegundamano/{filename}", produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE])
    fun getMotoImage(@PathVariable filename: String): ByteArray {
        val file = File("/app/uploads/imagenes/motosegundamano/$filename")
        if (!file.exists()) throw RuntimeException("Imagen no encontrada")
        return file.readBytes()
    }
}