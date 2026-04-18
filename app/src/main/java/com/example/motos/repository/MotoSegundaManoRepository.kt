package com.example.motos.repository

import com.example.motos.model.ImagenMoto
import com.example.motos.model.MotoSegundaMano
import com.example.motos.model.MotoSegundaManoRequest
import com.example.motos.model.Reserva
import com.example.motos.model.ReservaRequest
import com.example.motos.network.ApiService
import okhttp3.MultipartBody

class MotoSegundaManoRepository(private val api: ApiService) {

    suspend fun getAll(): List<MotoSegundaMano> {
        val response = api.getMotosSegundaMano()
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    suspend fun filtrar(
        marca: String? = null,
        modelo: String? = null,
        cvMax: Int? = null,
        km: Int? = null,
        cilindradaMax: Int? = null,
        precioMax: Double? = null,
        matricula: String? = null
    ): List<MotoSegundaMano> {
        val response = api.filtrarMotosSegundaMano(marca, modelo, cvMax, km, cilindradaMax, precioMax, matricula)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    suspend fun crearReserva(request: ReservaRequest): Reserva? {
        val response = api.crearReserva(request)
        return if (response.isSuccessful) response.body() else null
    }


    suspend fun actualizarReserva(id: Long, request: ReservaRequest): Reserva? {
        val response = api.actualizarReserva(id, request)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getImagenes(motoId: Long): List<ImagenMoto> {
        val response = api.getImagenesMoto(motoId)
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    suspend fun subirImagen(motoId: okhttp3.RequestBody, file: MultipartBody.Part): ImagenMoto? {
        val response = api.subirImagen(motoId, file)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun eliminarImagen(id: Long) {
        api.eliminarImagen(id)
    }

    suspend fun actualizarMoto(id: Long, request: MotoSegundaManoRequest): MotoSegundaMano? {
        val response = api.actualizarMoto(id, request)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun eliminarMoto(id: Long) {
        api.eliminarMoto(id)
    }

    suspend fun crearMoto(request: MotoSegundaManoRequest): MotoSegundaMano? {
        val response = api.crearMoto(request)
        return if (response.isSuccessful) response.body() else null
    }




    suspend fun getReservasByCliente(clienteId: Long): List<Reserva> {
        val response = api.getReservasByCliente(clienteId)
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    suspend fun getTodasReservas(): List<Reserva> {
        val response = api.getReservas()
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    suspend fun actualizarEstadoReserva(id: Long, estado: String): Reserva? {
        val response = api.actualizarEstadoReserva(id, estado)
        return if (response.isSuccessful) response.body() else null
    }
}