package com.example.myapplication.repository

import com.example.myapplication.model.CepData

interface CepRepository {
    suspend fun getCepInfo(cep: String): CepData?
}
