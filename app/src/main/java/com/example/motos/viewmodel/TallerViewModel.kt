package com.example.motos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.motos.model.Cita
import com.example.motos.model.CitaRequest
import com.example.motos.model.MecanicoSimple
import com.example.motos.model.MotoCliente
import com.example.motos.model.MotoClienteRequest
import com.example.motos.model.Reparacion
import com.example.motos.model.ReparacionRequest
import com.example.motos.repository.TallerRepository
import kotlinx.coroutines.launch

class TallerViewModel(private val repository: TallerRepository) : ViewModel() {

    private val _motos = MutableLiveData<List<MotoCliente>>()
    val motos: LiveData<List<MotoCliente>> = _motos

    private val _citas = MutableLiveData<List<Cita>>()
    val citas: LiveData<List<Cita>> = _citas

    private val _reparaciones = MutableLiveData<List<Reparacion>>()
    val reparaciones: LiveData<List<Reparacion>> = _reparaciones

    private val _accionCompleta = MutableLiveData<Boolean>()
    val accionCompleta: LiveData<Boolean> = _accionCompleta

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _citaActual = MutableLiveData<Cita?>()
    val citaActual: LiveData<Cita?> = _citaActual

    private val _reparacionActual = MutableLiveData<Reparacion?>()
    val reparacionActual: LiveData<Reparacion?> = _reparacionActual

    fun cargarCitaYReparacion(citaId: Long, rol: String, clienteId: Long) {
        viewModelScope.launch {
            val citas = when (rol) {
                "CLIENTE" -> repository.getCitasCliente(clienteId)
                else -> repository.getCitas()
            }
            _citaActual.value = citas.find { it.id == citaId }

            val reparaciones = when (rol) {
                "CLIENTE" -> repository.getReparacionesByCliente(clienteId)
                else -> repository.getReparaciones()
            }
            _reparacionActual.value = reparaciones.find { it.cita.id == citaId }
        }
    }



    fun cargarMotosCliente(clienteId: Long) {
        viewModelScope.launch {
            _motos.value = repository.getMotosCliente(clienteId)
        }
    }

    fun crearMotoCliente(request: MotoClienteRequest) {
        viewModelScope.launch {
            val r = repository.crearMotoCliente(request)
            if (r != null) _accionCompleta.value = true
            else _error.value = "Error al crear moto (puede que ya exista la matrícula)"
        }
    }

    fun eliminarMotoCliente(matricula: String) {
        viewModelScope.launch {
            if (repository.eliminarMotoCliente(matricula)) _accionCompleta.value = true
            else _error.value = "Error al eliminar"
        }
    }

    fun cargarCitas() {
        viewModelScope.launch {
            _citas.value = repository.getCitas()
                .sortedWith(compareByDescending<Cita> { it.fecha }.thenByDescending { it.hora })
        }
    }


    fun resetAccionCompleta() {
        _accionCompleta.value = false
    }

    fun resetError() {
        _error.value = null
    }
    fun cargarCitasCliente(clienteId: Long) {
        viewModelScope.launch {
            _citas.value = repository.getCitasCliente(clienteId)
                .sortedWith(compareByDescending<Cita> { it.fecha }.thenByDescending { it.hora })
        }
    }

    fun crearCita(request: CitaRequest) {
        viewModelScope.launch {
            val r = repository.crearCita(request)
            if (r != null) _accionCompleta.value = true
            else _error.value = "Error al crear cita"
        }
    }

    fun actualizarEstadoCita(id: Long, estado: String, mecanicoId: Long? = null) {
        viewModelScope.launch {
            repository.actualizarEstadoCita(id, estado, mecanicoId)
            _accionCompleta.value = true
        }
    }

    fun cargarReparacionesMoto(matricula: String) {
        viewModelScope.launch { _reparaciones.value = repository.getReparacionesByMoto(matricula) }
    }

    fun cargarReparaciones() {
        viewModelScope.launch { _reparaciones.value = repository.getReparaciones() }
    }

    fun crearReparacion(request: ReparacionRequest) {
        viewModelScope.launch {
            val r = repository.crearReparacion(request)
            if (r != null) _accionCompleta.value = true
            else _error.value = "Error al crear presupuesto"
        }
    }

    fun actualizarEstadoReparacion(id: Long, estado: String) {
        viewModelScope.launch {
            repository.actualizarEstadoReparacion(id, estado)
            _accionCompleta.value = true
        }
    }

    fun actualizarReparacion(id: Long, request: ReparacionRequest) {
        viewModelScope.launch {
            val r = repository.actualizarReparacion(id, request)
            if (r != null) _accionCompleta.value = true
            else _error.value = "Error al actualizar presupuesto"
        }
    }

    fun actualizarCita(id: Long, request: CitaRequest) {
        viewModelScope.launch {
            val r = repository.actualizarCita(id, request)
            if (r != null) _accionCompleta.value = true
            else _error.value = "Error al modificar cita"
        }
    }

    fun eliminarCita(id: Long) {
        viewModelScope.launch {
            if (repository.eliminarCita(id)) _accionCompleta.value = true
            else _error.value = "Error al eliminar cita"
        }
    }

    suspend fun comprobarDisponibilidad(fecha: String, hora: String): Boolean {
        return repository.getMecanicosDisponibles(fecha, hora).isNotEmpty()
    }

    suspend fun getMecanicosDisponiblesAhora(fecha: String, hora: String): List<com.example.motos.model.MecanicoSimple> {
        val ids = repository.getMecanicosDisponibles(fecha, hora)
        val todos = repository.getMecanicos()
        return todos.filter { it.id in ids }
    }



    private val _mecanicos = MutableLiveData<List<MecanicoSimple>>()
    val mecanicos: LiveData<List<MecanicoSimple>> = _mecanicos

    fun cargarMecanicos() {
        viewModelScope.launch {
            _mecanicos.value = repository.getMecanicos()
        }
    }

    fun cargarReparacionesCliente(clienteId: Long) {
        viewModelScope.launch {
            _reparaciones.value = repository.getReparacionesByCliente(clienteId)
        }
    }
}

class TallerViewModelFactory(private val repository: TallerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TallerViewModel(repository) as T
    }



}