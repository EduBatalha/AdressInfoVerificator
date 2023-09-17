package com.example.myapplication.network

import com.example.myapplication.model.CepData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepService {
    @GET("{cep}/json")
    suspend fun getCepDetails(@Path("cep") cep: String): CepData
}

