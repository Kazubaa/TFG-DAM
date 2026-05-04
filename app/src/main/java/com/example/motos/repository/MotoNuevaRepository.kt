package com.example.motos.repository

import com.example.motos.model.ImagenMotoNueva
import com.example.motos.model.MotoNueva
import com.example.motos.model.MotoNuevaRequest
import com.example.motos.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MotoNuevaRepository(private val api: ApiService) {


    suspend fun get(id: Long): MotoNueva? {
        val r = api.getMotoNueva(id)
        return if (r.isSuccessful) r.body() else null
    }


    suspend fun getByMarcaCategoria(marca: String, categoria: String): List<MotoNueva> {
        val r = api.getMotosNuevasByMarcaCategoria(marca, categoria)
        return if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
    }

    suspend fun crear(request: MotoNuevaRequest): MotoNueva? {
        val r = api.crearMotoNueva(request)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun actualizar(id: Long, request: MotoNuevaRequest): MotoNueva? {
        val r = api.actualizarMotoNueva(id, request)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun eliminar(id: Long): Boolean {
        val r = api.eliminarMotoNueva(id)
        return r.isSuccessful
    }


    suspend fun getImagenesPorTipo(motoId: Long, tipo: String): List<ImagenMotoNueva> {
        val r = api.getImagenesMotoNuevaPorTipo(motoId, tipo)
        return if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
    }

    suspend fun subirImagen(motoId: Long, file: File, tipo: String): ImagenMotoNueva? {
        val mediaType = "image/*".toMediaTypeOrNull()
        val body = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody(mediaType))
        val motoIdBody = motoId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val tipoBody = tipo.toRequestBody("text/plain".toMediaTypeOrNull())
        val r = api.subirImagenMotoNueva(motoIdBody, body, tipoBody)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun eliminarImagen(id: Long): Boolean {
        val r = api.eliminarImagenMotoNueva(id)
        return r.isSuccessful
    }

    suspend fun subirVideo(motoId: Long, file: java.io.File): String? {
        val mediaType = "video/*".toMediaTypeOrNull()
        val body = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody(mediaType))
        val motoIdBody = motoId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val r = api.subirVideoMotoNueva(motoIdBody, body)
        return if (r.isSuccessful) r.body()?.get("url") else null
    }

    suspend fun eliminarVideo(motoId: Long): Boolean {
        val r = api.eliminarVideoMotoNueva(motoId)
        return r.isSuccessful
    }
}