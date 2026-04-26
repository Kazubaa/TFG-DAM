package com.example.motos.viewmodel

import androidx.lifecycle.*
import com.example.motos.model.Cliente
import com.example.motos.model.ClienteRequest
import com.example.motos.model.Mecanico
import com.example.motos.model.MecanicoRequest
import com.example.motos.model.Vendedor
import com.example.motos.model.VendedorRequest
import com.example.motos.repository.ClienteRepository
import com.example.motos.repository.PerfilRepository
import kotlinx.coroutines.launch

class PerfilViewModel(private val clienteRepo: ClienteRepository, private val perfilRepo: PerfilRepository) : ViewModel() {

    private val _cliente = MutableLiveData<Cliente?>()
    val cliente: LiveData<Cliente?> = _cliente

    private val _mecanico = MutableLiveData<Mecanico?>()
    val mecanico: LiveData<Mecanico?> = _mecanico

    private val _vendedor = MutableLiveData<Vendedor?>()
    val vendedor: LiveData<Vendedor?> = _vendedor

    private val _actualizacionCompleta = MutableLiveData<Boolean>()
    val actualizacionCompleta: LiveData<Boolean> = _actualizacionCompleta

    fun cargarCliente(id: Long) {
        viewModelScope.launch { _cliente.value = clienteRepo.getCliente(id) }
    }

    fun actualizarCliente(id: Long, request: ClienteRequest) {
        viewModelScope.launch {
            val r = clienteRepo.actualizarCliente(id, request)
            _actualizacionCompleta.value = r != null
        }
    }

    fun cargarMecanico(id: Long) {
        viewModelScope.launch { _mecanico.value = perfilRepo.getMecanico(id) }
    }

    fun actualizarMecanico(id: Long, request: MecanicoRequest) {
        viewModelScope.launch {
            val r = perfilRepo.actualizarMecanico(id, request)
            _actualizacionCompleta.value = r != null
        }
    }

    fun cargarVendedor(id: Long) {
        viewModelScope.launch { _vendedor.value = perfilRepo.getVendedor(id) }
    }

    fun actualizarVendedor(id: Long, request: VendedorRequest) {
        viewModelScope.launch {
            val r = perfilRepo.actualizarVendedor(id, request)
            _actualizacionCompleta.value = r != null
        }
    }
}


class PerfilViewModelFactory(private val clienteRepo: ClienteRepository, private val perfilRepo: PerfilRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PerfilViewModel(clienteRepo, perfilRepo) as T
    }
}