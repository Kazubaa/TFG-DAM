package com.example.motos.viewmodel

import androidx.lifecycle.*
import com.example.motos.model.Reserva
import com.example.motos.repository.MotoSegundaManoRepository
import kotlinx.coroutines.launch

class ReservaViewModel(private val repository: MotoSegundaManoRepository) : ViewModel() {

    private val _reservas = MutableLiveData<List<Reserva>>()
    val reservas: LiveData<List<Reserva>> = _reservas

    private val _accionCompletada = MutableLiveData<Boolean>()
    val accionCompletada: LiveData<Boolean> = _accionCompletada

    fun cargarReservasCliente(clienteId: Long) {
        viewModelScope.launch {
            _reservas.value = repository.getReservasByCliente(clienteId)
        }
    }

    fun cargarTodasReservas() {
        viewModelScope.launch {
            _reservas.value = repository.getTodasReservas()
        }
    }

    fun actualizarEstado(id: Long, estado: String) {
        viewModelScope.launch {
            repository.actualizarEstadoReserva(id, estado)
            _accionCompletada.value = true
        }
    }
}

class ReservaViewModelFactory(private val repository: MotoSegundaManoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReservaViewModel(repository) as T
    }
}