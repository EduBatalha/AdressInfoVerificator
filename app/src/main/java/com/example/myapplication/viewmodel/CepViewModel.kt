package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.CepRepository
import com.example.myapplication.model.CepData
import kotlinx.coroutines.launch

class CepViewModel(private val repository: CepRepository) : ViewModel() {
    private val _cepDetails = MutableLiveData<CepData>()
    val cepDetails: LiveData<CepData>
        get() = _cepDetails

    fun fetchCepDetails(cep: String) {
        viewModelScope.launch {
            try {
                val response = repository.getCepInfo(cep)
                _cepDetails.postValue(response)
            } catch (e: Exception) {
                // Lide com erros aqui
            }
        }
    }
}
