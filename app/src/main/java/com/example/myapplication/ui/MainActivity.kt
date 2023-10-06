package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.model.Database.AppDatabase
import com.example.myapplication.model.Entity.RetrofitClient
import com.example.myapplication.model.dao.SearchHistoryDao
import com.example.myapplication.network.ConnectivityManager
import com.example.myapplication.repository.CepRepositoryImpl
import com.example.myapplication.viewmodel.CepViewModel
import com.example.myapplication.viewmodel.CepViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CepViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viaCepService = RetrofitClient.viaCepService
        val repository = CepRepositoryImpl(viaCepService)
        val appDatabase = AppDatabase.getInstance(applicationContext)
        val searchHistoryDao = appDatabase.searchHistoryDao()
        val connectivityManager = ConnectivityManager(this)

        viewModel = ViewModelProvider(
            this,
            CepViewModelFactory(repository, connectivityManager, searchHistoryDao)
        ).get(CepViewModel::class.java)

        val editTextCep = findViewById<EditText>(R.id.editTextCep)
        val buttonSearch = findViewById<Button>(R.id.buttonSearch)
        val textViewDetails = findViewById<TextView>(R.id.textViewDetails)

        // Configurar um KeyListener para o EditText para detectar a tecla "Enter"
        editTextCep.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // A tecla "Enter" foi pressionada, execute a ação desejada aqui.
                buttonSearch.performClick() // Isso acionará o clique do botão de pesquisa.
                return@setOnKeyListener true
            }
            false
        }

        buttonSearch.setOnClickListener {
            val cep = editTextCep.text.toString()
            viewModel.fetchCepDetails(cep)

            // Adicione a consulta do CEP ao histórico
            addToSearchHistory(cep)
        }

        val buttonHistory = findViewById<Button>(R.id.searchHistory)

        buttonHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
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

        // Mantenha o método para lidar com mensagens de erro
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

    private fun addToSearchHistory(cep: String) {
        // Adicione o CEP ao histórico no banco de dados
        viewModel.viewModelScope.launch {
            viewModel.insertSearchHistory(cep)
        }
    }
}
