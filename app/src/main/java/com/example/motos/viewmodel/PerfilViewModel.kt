package com.example.motos.viewmodel

import androidx.lifecycle.*
import com.example.motos.model.Cliente
import com.example.motos.model.ClienteRequest
import com.example.motos.repository.ClienteRepository
import kotlinx.coroutines.launch

class PerfilViewModel(private val repository: ClienteRepository) : ViewModel() {

    private val _cliente = MutableLiveData<Cliente?>()
    val cliente: LiveData<Cliente?> = _cliente

    private val _actualizacionCompleta = MutableLiveData<Boolean>()
    val actualizacionCompleta: LiveData<Boolean> = _actualizacionCompleta

    fun cargarPerfil(clienteId: Long) {
        viewModelScope.launch {
            _cliente.value = repository.getCliente(clienteId)
        }
    }

    fun actualizarPerfil(id: Long, request: ClienteRequest) {
        viewModelScope.launch {
            val result = repository.actualizarCliente(id, request)
            _actualizacionCompleta.value = result != null
        }
    }
}

class PerfilViewModelFactory(private val repository: ClienteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PerfilViewModel(repository) as T
    }
}