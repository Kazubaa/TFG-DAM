package com.tfg.concesionario.service

import com.tfg.concesionario.model.Imagen
import com.tfg.concesionario.repository.ImagenRepository
import com.tfg.concesionario.repository.MotoSegundaManoRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths

@Service
class ImagenService(
    private val imagenRepo: ImagenRepository,
    private val motoRepo: MotoSegundaManoRepository
) {

    private val uploadDir = "/app/uploads/imagenes/motosegundamano/"

    fun subirImagen(file: MultipartFile, motoId: Long): Imagen {

        val moto = motoRepo.findById(motoId)
            .orElseThrow { RuntimeException("Moto no encontrada") }

        val fileName = System.currentTimeMillis().toString() + "_" + file.originalFilename
        val path = Paths.get(uploadDir + fileName)

        Files.createDirectories(path.parent)
        Files.write(path, file.bytes)

        val imagen = Imagen(
            url = "motosegundamano/$fileName",
            motoSegundaMano = moto
        )

        val saved = imagenRepo.save(imagen)

        //asigna imagen principal automaticamente
        if (moto.imagenPrincipal == null) {
            moto.imagenPrincipal = saved.url
            motoRepo.save(moto)
        }

        return saved
    }

    fun eliminarImagen(id: Long) {
        val imagen = imagenRepo.findById(id)
            .orElseThrow { RuntimeException("Imagen no encontrada") }

        Files.deleteIfExists(Paths.get("/app/imagenes/segunda/" + imagen.url))
        imagenRepo.delete(imagen)
    }

    fun setPrincipal(motoId: Long, url: String) {

        val moto = motoRepo.findById(motoId)
            .orElseThrow { RuntimeException("Moto no encontrada") }

        if (!moto.imagenes.any { it.url == url }) {
            throw RuntimeException("La imagen no pertenece a la moto")
        }

        moto.imagenPrincipal = url
        motoRepo.save(moto)
    }

    fun getImagenes(motoId: Long): List<Imagen> {
        return imagenRepo.findByMotoSegundaManoId(motoId)
    }
}