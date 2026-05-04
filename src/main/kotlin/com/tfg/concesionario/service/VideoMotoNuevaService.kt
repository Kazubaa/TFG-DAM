package com.tfg.concesionario.service

import com.tfg.concesionario.repository.MotoNuevaRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class VideoMotoNuevaService(private val motoRepo: MotoNuevaRepository) {

    private val baseDir = "/app/uploads/videos/motonueva"

    fun subir(motoId: Long, file: MultipartFile): String {
        val dir = File(baseDir).apply { if (!exists()) mkdirs() }
        val filename = "${System.currentTimeMillis()}_${file.originalFilename}"
        val target = File(dir, filename)
        file.transferTo(target)

        val ruta = "videos/motonueva/$filename"
        val moto = motoRepo.findById(motoId).orElseThrow { RuntimeException("Moto no encontrada") }
        motoRepo.save(moto.copy(videoFile = ruta))

        return ruta
    }

    fun eliminar(motoId: Long) {
        val moto = motoRepo.findById(motoId).orElseThrow { RuntimeException("Moto no encontrada") }
        moto.videoFile?.let {
            val file = File("/app/uploads/$it")
            if (file.exists()) file.delete()
        }
        motoRepo.save(moto.copy(videoFile = null))
    }
}