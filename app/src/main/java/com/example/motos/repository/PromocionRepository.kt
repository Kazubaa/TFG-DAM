package com.example.motos.repository

import com.example.motos.network.ApiService

class PromocionRepository(private val api: ApiService) {

    suspend fun getPromocionesMotos(): List<String> {
        val response = api.getPromocionesMotos()
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    suspend fun getPromocionesMecanico(): List<String> {
        val response = api.getPromocionesMecanico()
        return if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }
}