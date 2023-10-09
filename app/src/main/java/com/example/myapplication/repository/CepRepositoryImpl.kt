package com.example.myapplication.repository

import com.example.myapplication.model.Entity.CepData
import com.example.myapplication.network.ViaCepService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class CepRepositoryImpl(private val viaCepService: ViaCepService) : CepRepository {

    override suspend fun getCepInfo(cep: String): CepData? {
        return try {
            val cepResponse = withContext(Dispatchers.IO) {
                // Executa a chamada à API em uma corrotina com Dispatchers.IO
                viaCepService.getCepDetails(cep)
            }

            if (cepResponse != null) {
                // Verifique se a resposta não é nula
                cepResponse
            } else {
                null
            }
        } catch (e: HttpException) {
            // Lida com erros HTTP
            e.printStackTrace()
            null
        } catch (e: IOException) {
            // Lida com exceções de rede
            e.printStackTrace()
            null
        } catch (e: Exception) {
            // Lida com outras exceções
            e.printStackTrace()
            null
        }
    }
}
