package com.example.motos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.motos.model.ImagenMotoNueva
import com.example.motos.model.MotoNueva
import com.example.motos.model.MotoNuevaRequest
import com.example.motos.repository.MotoNuevaRepository
import kotlinx.coroutines.launch
import java.io.File

class MotoNuevaViewModel(private val repo: MotoNuevaRepository) : ViewModel() {

    private val _motos = MutableLiveData<List<MotoNueva>>()
    val motos: LiveData<List<MotoNueva>> = _motos

    private val _motoActual = MutableLiveData<MotoNueva?>()
    val motoActual: LiveData<MotoNueva?> = _motoActual

    private val _imagenesGaleria = MutableLiveData<List<ImagenMotoNueva>>()
    val imagenesGaleria: LiveData<List<ImagenMotoNueva>> = _imagenesGaleria

    private val _imagenes360 = MutableLiveData<List<ImagenMotoNueva>>()
    val imagenes360: LiveData<List<ImagenMotoNueva>> = _imagenes360

    private val _accionCompleta = MutableLiveData<Boolean>()
    val accionCompleta: LiveData<Boolean> = _accionCompleta

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error


    fun cargarPorMarcaCategoria(marca: String, categoria: String) {
        viewModelScope.launch { _motos.value = repo.getByMarcaCategoria(marca, categoria) }
    }

    fun cargarMoto(id: Long) {
        viewModelScope.launch {
            _motoActual.value = repo.get(id)
            _imagenesGaleria.value = repo.getImagenesPorTipo(id, "GALERIA")
            _imagenes360.value = repo.getImagenesPorTipo(id, "R360")
        }
    }

    fun crear(request: MotoNuevaRequest) {
        viewModelScope.launch {
            val r = repo.crear(request)
            if (r != null) {
                _motoActual.value = r
                _accionCompleta.value = true
            } else _error.value = "Error al crear moto"
        }
    }

    fun actualizar(id: Long, request: MotoNuevaRequest) {
        viewModelScope.launch {
            val r = repo.actualizar(id, request)
            if (r != null) _accionCompleta.value = true
            else _error.value = "Error al actualizar"
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            if (repo.eliminar(id)) _accionCompleta.value = true
            else _error.value = "Error al eliminar"
        }
    }

    fun subirImagen(motoId: Long, file: File, tipo: String) {
        viewModelScope.launch {
            val r = repo.subirImagen(motoId, file, tipo)
            if (r != null) {
                if (tipo == "GALERIA") _imagenesGaleria.value = repo.getImagenesPorTipo(motoId, "GALERIA")
                else _imagenes360.value = repo.getImagenesPorTipo(motoId, "R360")
            } else _error.value = "Error al subir imagen"
        }
    }

    fun eliminarImagen(id: Long, motoId: Long, tipo: String) {
        viewModelScope.launch {
            if (repo.eliminarImagen(id)) {
                if (tipo == "GALERIA") _imagenesGaleria.value = repo.getImagenesPorTipo(motoId, "GALERIA")
                else _imagenes360.value = repo.getImagenesPorTipo(motoId, "R360")
            }
        }
    }

    fun resetAccionCompleta() { _accionCompleta.value = false }
    fun resetError() { _error.value = null }


    fun subirVideo(motoId: Long, file: java.io.File) {
        viewModelScope.launch {
            val r = repo.subirVideo(motoId, file)
            if (r != null) {
                cargarMoto(motoId)
            } else _error.value = "Error al subir vídeo"
        }
    }

    fun eliminarVideo(motoId: Long) {
        viewModelScope.launch {
            if (repo.eliminarVideo(motoId)) {
                cargarMoto(motoId)
            }
        }
    }
}

class MotoNuevaViewModelFactory(private val repo: MotoNuevaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MotoNuevaViewModel(repo) as T
    }
}