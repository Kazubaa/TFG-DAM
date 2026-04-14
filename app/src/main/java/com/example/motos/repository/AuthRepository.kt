package com.example.motos.repository

import com.example.motos.model.LoginResponse
import com.example.motos.model.LoginRequest
import com.example.motos.model.RegisterRequest
import com.example.motos.network.ApiService
import retrofit2.Response

class AuthRepository(private val api: ApiService) {

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return api.login(LoginRequest(username, password))
    }

    suspend fun register(username: String, password: String): Response<LoginResponse> {
        return api.register(RegisterRequest(username, password))
    }
}