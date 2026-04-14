package com.example.motos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.motos.repository.PromocionRepository

class PromocionViewModelFactory(private val repository: PromocionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PromocionViewModel(repository) as T
    }
}