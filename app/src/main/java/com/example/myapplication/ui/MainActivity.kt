package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.model.Entity.RetrofitClient
import com.example.myapplication.network.ConnectivityManager
import com.example.myapplication.repository.CepRepositoryImpl
import com.example.myapplication.viewmodel.CepViewModel
import com.example.myapplication.viewmodel.CepViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CepViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viaCepService = RetrofitClient.viaCepService
        val repository = CepRepositoryImpl(viaCepService)
        val connectivityManager = ConnectivityManager(this)

        viewModel = ViewModelProvider(this, CepViewModelFactory(repository, connectivityManager))
            .get(CepViewModel::class.java)

        val editTextCep = findViewById<EditText>(R.id.editTextCep)
        val buttonSearch = findViewById<Button>(R.id.buttonSearch)
        val textViewDetails = findViewById<TextView>(R.id.textViewDetails)

        buttonSearch.setOnClickListener {
            val cep = editTextCep.text.toString()
            viewModel.fetchCepDetails(cep)
        }

        viewModel.cepDetails.observe(this) { cepData ->
            if (cepData != null) {
                // Exibe os detalhes do CEP
                textViewDetails.text = "CEP: ${cepData.cep}\n" +
                        "Logradouro: ${cepData.logradouro}\n" +
                        "Complemento: ${cepData.complemento}\n" +
                        "Bairro: ${cepData.bairro}\n" +
                        "Cidade: ${cepData.localidade}\n" +
                        "Estado: ${cepData.uf}\n" +
                        "IBGE: ${cepData.ibge}\n" +
                        "GIA: ${cepData.gia}\n" +
                        "DDD: ${cepData.ddd}\n" +
                        "SIAFI: ${cepData.siafi}\n"
            } else {
                // Verifica se a mensagem de erro é relacionada à falta de conexão com a Internet
                val errorMessage = viewModel.error.value
                if (errorMessage != null && errorMessage.contains("Verifique sua conexão com a Internet")) {
                    textViewDetails.text = "Sem conexão com a Internet. Por favor, verifique sua conexão e tente novamente."
                } else {
                    // Exibe a mensagem de erro padrão
                    textViewDetails.text = "CEP não encontrado."
                }
            }
        }


        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrBlank()) {
                if (errorMessage.contains("Verifique sua conexão com a Internet")) {
                    textViewDetails.text = "Sem conexão com a Internet. Por favor, verifique sua conexão e tente novamente."
                } else {
                    textViewDetails.text = errorMessage
                }
            }
        }

    }
}
