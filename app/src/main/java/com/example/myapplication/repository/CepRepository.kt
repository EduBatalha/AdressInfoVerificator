package com.example.myapplication.repository

import com.example.myapplication.model.Entity.CepData

interface CepRepository {
    suspend fun getCepInfo(cep: String): CepData?
}
