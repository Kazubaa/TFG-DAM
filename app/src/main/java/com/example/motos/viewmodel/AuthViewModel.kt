package com.example.motos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motos.model.LoginResponse
import com.example.motos.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class AuthState {
    object Loading : AuthState()
    data class Success(val data: LoginResponse) : AuthState()
    object RegisterSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.login(username, password)
                if (response.isSuccessful && response.body() != null) {
                    _authState.value = AuthState.Success(response.body()!!)
                } else {
                    _authState.value = AuthState.Error("Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun register(username: String, password: String, email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.register(username, password, email)
                if (response.isSuccessful) {
                    _authState.value = AuthState.RegisterSuccess
                } else {
                    val errorMsg = try {
                        val errorBody = response.errorBody()?.string()
                        val json = org.json.JSONObject(errorBody ?: "")
                        val msg = json.optString("message", "")
                        when {
                            msg.contains("email", ignoreCase = true) ->
                                "El correo ${email} ya está en uso, usa otro por favor"
                            msg.contains("username", ignoreCase = true) ->
                                "Ese nombre de usuario ya está en uso"
                            msg.isNotBlank() -> msg
                            else -> "Error al registrarse"
                        }
                    } catch (e: Exception) {
                        "Error al registrarse"
                    }
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}