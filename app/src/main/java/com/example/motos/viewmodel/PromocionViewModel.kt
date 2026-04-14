package com.example.motos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motos.repository.PromocionRepository
import kotlinx.coroutines.launch

class PromocionViewModel(private val repository: PromocionRepository) : ViewModel() {

    private val _promocionesMotos = MutableLiveData<List<String>>()
    val promocionesMotos: LiveData<List<String>> = _promocionesMotos

    private val _promocionesMecanico = MutableLiveData<List<String>>()
    val promocionesMecanico: LiveData<List<String>> = _promocionesMecanico

    fun cargarPromociones() {
        viewModelScope.launch {
            _promocionesMotos.value = repository.getPromocionesMotos()
            _promocionesMecanico.value = repository.getPromocionesMecanico()
        }
    }
}