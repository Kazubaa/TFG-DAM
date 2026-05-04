package com.tfg.concesionario.service

import com.tfg.concesionario.model.ImagenMotoNueva
import com.tfg.concesionario.repository.ImagenMotoNuevaRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class ImagenMotoNuevaService(private val repo: ImagenMotoNuevaRepository) {

    private val baseDir = "/app/uploads/imagenes/motonueva"

    fun getByMoto(motoId: Long): List<ImagenMotoNueva> =
        repo.findByMotoNuevaIdOrderByOrdenAsc(motoId)

    fun getByMotoYTipo(motoId: Long, tipo: String): List<ImagenMotoNueva> =
        repo.findByMotoNuevaIdAndTipoOrderByOrdenAsc(motoId, tipo)

    fun subir(motoId: Long, file: MultipartFile, tipo: String): ImagenMotoNueva {
        val dir = File(baseDir).apply { if (!exists()) mkdirs() }
        val filename = "${System.currentTimeMillis()}_${file.originalFilename}"
        val target = File(dir, filename)
        file.transferTo(target)

        val orden = repo.findByMotoNuevaIdAndTipoOrderByOrdenAsc(motoId, tipo).size

        return repo.save(ImagenMotoNueva(
            motoNuevaId = motoId,
            url = "motonueva/$filename",
            tipo = tipo,
            orden = orden
        ))
    }

    fun eliminar(id: Long) = repo.deleteById(id)
}