package com.example.motos.repository

import com.example.motos.model.Cliente
import com.example.motos.model.ClienteRequest
import com.example.motos.network.ApiService

class ClienteRepository(private val api: ApiService) {

    suspend fun getCliente(id: Long): Cliente? {
        val response = api.getCliente(id)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun actualizarCliente(id: Long, request: ClienteRequest): Cliente? {
        val response = api.actualizarCliente(id, request)
        return if (response.isSuccessful) response.body() else null
    }
}