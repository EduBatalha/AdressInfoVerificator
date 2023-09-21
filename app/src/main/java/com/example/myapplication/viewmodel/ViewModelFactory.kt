package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import com.example.myapplication.network.ConnectivityManager
import com.example.myapplication.repository.CepRepository

class CepViewModelFactory(
    private val repository: CepRepository,
    private val connectivityManager: ConnectivityManager
) : Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CepViewModel::class.java)) {
            return CepViewModel(repository, connectivityManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
