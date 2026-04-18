package com.example.motos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.motos.repository.MotoSegundaManoRepository

class MotoSegundaManoViewModelFactory(
    private val repository: MotoSegundaManoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MotoSegundaManoViewModel(repository) as T
    }
}