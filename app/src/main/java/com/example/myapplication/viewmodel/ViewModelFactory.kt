package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.model.dao.SearchHistoryDao
import com.example.myapplication.network.ConnectivityManager
import com.example.myapplication.repository.CepRepository

class CepViewModelFactory(
    private val repository: CepRepository,
    private val connectivityManager: ConnectivityManager,
    private val searchHistoryDao: SearchHistoryDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CepViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CepViewModel(repository, connectivityManager, searchHistoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

}
