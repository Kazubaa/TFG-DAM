package com.example.motos.network

import com.example.motos.model.LoginRequest
import com.example.motos.model.LoginResponse
import com.example.motos.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>


    @GET("imagenes/promociones")
    suspend fun getPromocionesMotos(): Response<List<String>>

    @GET("imagenes/promomecanico")
    suspend fun getPromocionesMecanico(): Response<List<String>>
}