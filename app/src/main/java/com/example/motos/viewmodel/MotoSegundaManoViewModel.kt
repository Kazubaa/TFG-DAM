package com.example.motos.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motos.model.ImagenMoto
import com.example.motos.model.MotoSegundaMano
import com.example.motos.model.MotoSegundaManoRequest
import com.example.motos.model.ReservaRequest
import com.example.motos.repository.MotoSegundaManoRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

sealed class MotoState {
    object Loading : MotoState()
    data class Success(val data: List<MotoSegundaMano>) : MotoState()
    data class Error(val message: String) : MotoState()
}

sealed class ReservaState {
    object Idle : ReservaState()
    object Loading : ReservaState()
    object Success : ReservaState()
    data class Error(val message: String) : ReservaState()
}

class MotoSegundaManoViewModel(private val repository: MotoSegundaManoRepository) : ViewModel() {

    private val _motoState = MutableLiveData<MotoState>()
    val motoState: LiveData<MotoState> = _motoState

    private val _reservaState = MutableLiveData<ReservaState>(ReservaState.Idle)
    val reservaState: LiveData<ReservaState> = _reservaState

    fun cargarMotos(rol: String = "INVITADO") {
        viewModelScope.launch {
            _motoState.value = MotoState.Loading
            try {
                val todas = repository.getAll()
                val motos = if (rol == "ADMIN" || rol == "VENDEDOR") todas
                else todas.filter { it.disponible }
                _motoState.value = MotoState.Success(motos)
            } catch (e: Exception) {
                _motoState.value = MotoState.Error("Error al cargar motos: ${e.message}")
            }
        }
    }

    fun filtrar(
        marca: String? = null,
        modelo: String? = null,
        cvMax: Int? = null,
        km: Int? = null,
        cilindradaMax: Int? = null,
        precioMax: Double? = null,
        matricula: String? = null
    ) {
        viewModelScope.launch {
            _motoState.value = MotoState.Loading
            try {
                val motos = repository.filtrar(marca, modelo, cvMax,km, cilindradaMax, precioMax, matricula)
                _motoState.value = MotoState.Success(motos)
            } catch (e: Exception) {
                _motoState.value = MotoState.Error("Error al filtrar: ${e.message}")
            }
        }
    }

    fun crearReserva(clienteId: Long, motoId: Long, fecha: String, hora: String) {
        viewModelScope.launch {
            _reservaState.value = ReservaState.Loading
            try {
                val result = repository.crearReserva(
                    ReservaRequest(clienteId, motoId, fecha, hora)
                )
                if (result != null) _reservaState.value = ReservaState.Success
                else _reservaState.value = ReservaState.Error("Error al crear reserva")
            } catch (e: Exception) {
                _reservaState.value = ReservaState.Error("Error: ${e.message}")
            }
        }
    }

    private val _imagenes = MutableLiveData<List<ImagenMoto>>()
    val imagenes: LiveData<List<ImagenMoto>> = _imagenes

    private val _updateState = MutableLiveData<Boolean>()
    val updateState: LiveData<Boolean> = _updateState

    private val _deleteState = MutableLiveData<Boolean>()
    val deleteState: LiveData<Boolean> = _deleteState

    fun cargarImagenes(motoId: Long) {
        viewModelScope.launch {
            try {
                val imgs = repository.getImagenes(motoId)
                _imagenes.value = imgs
            } catch (e: Exception) {
                _imagenes.value = emptyList()
            }
        }
    }

    fun subirImagen(motoId: okhttp3.RequestBody, file: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val result = repository.subirImagen(motoId, file)
                if (result != null) {
                    val current = _imagenes.value?.toMutableList() ?: mutableListOf()
                    current.add(result)
                    _imagenes.value = current
                }
            } catch (e: Exception) { }
        }
    }

    fun eliminarImagen(imagenId: Long) {
        viewModelScope.launch {
            try {
                repository.eliminarImagen(imagenId)
                val current = _imagenes.value?.toMutableList() ?: mutableListOf()
                current.removeIf { it.id == imagenId }
                _imagenes.value = current
            } catch (e: Exception) { }
        }
    }

    fun actualizarMoto(id: Long, request: MotoSegundaManoRequest) {
        viewModelScope.launch {
            try {
                val result = repository.actualizarMoto(id, request)
                _updateState.value = result != null
            } catch (e: Exception) {
                _updateState.value = false
            }
        }
    }

    fun eliminarMoto(id: Long) {
        viewModelScope.launch {
            try {
                repository.eliminarMoto(id)
                _deleteState.value = true
            } catch (e: Exception) {
                _deleteState.value = false
            }
        }
    }

    fun actualizarEstadoReserva(id: Long, estado: String) {
        viewModelScope.launch {
            try {
                repository.actualizarEstadoReserva(id, estado)
                _reservaState.value = ReservaState.Success
            } catch (e: Exception) {
                _reservaState.value = ReservaState.Error("Error al actualizar reserva")
            }
        }
    }

    fun crearMoto(
        request: MotoSegundaManoRequest,
        imagenes: List<Uri>,
        onComplete: (MotoSegundaMano?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val moto = repository.crearMoto(request)
                onComplete(moto)
            } catch (e: Exception) {
                onComplete(null)
            }
        }
    }

    fun subirImagenSilencio(
        motoId: okhttp3.RequestBody,
        file: MultipartBody.Part,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.subirImagen(motoId, file)
                onComplete()
            } catch (e: Exception) {
                onComplete()
            }
        }
    }
}