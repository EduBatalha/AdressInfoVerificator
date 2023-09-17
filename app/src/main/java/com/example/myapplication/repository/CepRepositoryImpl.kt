package com.example.myapplication.repository

import com.example.myapplication.model.CepData
import com.example.myapplication.network.ViaCepService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class CepRepositoryImpl(private val viaCepService: ViaCepService) : CepRepository {

    override suspend fun getCepInfo(cep: String): CepData? {
        return try {
            val cepResponse = withContext(Dispatchers.IO) {
                // Execute a chamada à API em uma corrotina com Dispatchers.IO
                viaCepService.getCepDetails(cep)
            }

            if (cepResponse != null) {
                // Verifique se a resposta não é nula
                cepResponse
            } else {
                // Lide com erros da API, se necessário
                null
            }
        } catch (e: HttpException) {
            // Lide com erros HTTP
            e.printStackTrace()
            null
        } catch (e: IOException) {
            // Lide com exceções de rede
            e.printStackTrace()
            null
        } catch (e: Exception) {
            // Lide com outras exceções
            e.printStackTrace()
            null
        }
    }
}
