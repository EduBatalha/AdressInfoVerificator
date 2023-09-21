package com.example.myapplication.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.CepRepository
import com.example.myapplication.model.CepData
import com.example.myapplication.network.ConnectivityManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CepViewModel(
    private val repository: CepRepository,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {
    private val _cepDetails = MutableLiveData<CepData>()
    val cepDetails: LiveData<CepData>
        get() = _cepDetails

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    @SuppressLint("NullSafeMutableLiveData")
    fun fetchCepDetails(cep: String) {
        if (!connectivityManager.isInternetAvailable()) {
            _error.postValue("Sem conexão com a Internet.")
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.getCepInfo(cep)
                if (response != null) {
                    _cepDetails.postValue(response)
                } else {
                    _error.postValue("Dados do CEP não encontrados.")
                }
            } catch (e: HttpException) {
                _error.postValue("Erro HTTP: ${e.code()}")
            } catch (e: IOException) {
                _error.postValue("Erro de rede: Verifique sua conexão com a Internet.")
            } catch (e: Exception) {
                _error.postValue("Erro desconhecido: ${e.message}")
            }
        }
    }
}
