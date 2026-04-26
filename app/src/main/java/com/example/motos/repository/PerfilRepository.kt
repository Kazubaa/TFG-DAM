package com.example.motos.repository

import com.example.motos.model.Mecanico
import com.example.motos.model.MecanicoRequest
import com.example.motos.model.Vendedor
import com.example.motos.model.VendedorRequest
import com.example.motos.network.ApiService

class PerfilRepository(private val api: ApiService) {

    suspend fun getMecanico(id: Long): Mecanico? {
        val r = api.getMecanico(id)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun actualizarMecanico(id: Long, request: MecanicoRequest): Mecanico? {
        val r = api.actualizarMecanico(id, request)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun getVendedor(id: Long): Vendedor? {
        val r = api.getVendedor(id)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun actualizarVendedor(id: Long, request: VendedorRequest): Vendedor? {
        val r = api.actualizarVendedor(id, request)
        return if (r.isSuccessful) r.body() else null
    }
}