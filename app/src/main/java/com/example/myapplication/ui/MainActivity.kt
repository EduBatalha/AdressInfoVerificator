package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.myapplication.network.ConnectivityManager
import com.example.myapplication.repository.CepRepositoryImpl
import com.example.myapplication.viewmodel.CepViewModel
import com.example.myapplication.viewmodel.CepViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CepViewModel
    private lateinit var editTextCep: EditText
    private lateinit var buttonSearch: Button
    private lateinit var textViewDetails: TextView

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

        editTextCep = findViewById(R.id.editTextCep)
        buttonSearch = findViewById(R.id.buttonSearch)
        textViewDetails = findViewById(R.id.textViewDetails)

        // Adicione este código para preencher o EditText com o valor do CEP
        val intent = intent
        val cepToSearch = intent.getStringExtra("cepToSearch")
        if (!cepToSearch.isNullOrBlank()) {
            editTextCep.setText(cepToSearch)
        }

        editTextCep.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                performCepSearch()
                return@setOnKeyListener true
            }
            false
        }

        buttonSearch.setOnClickListener {
            performCepSearch()
        }

        val buttonHistory = findViewById<Button>(R.id.searchHistory)

        buttonHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        viewModel.cepDetails.observe(this) { cepData ->
            if (cepData != null) {
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

                buttonSearch.isEnabled = false
            } else {
                val errorMessage = viewModel.error.value
                if (errorMessage != null && errorMessage.contains("Verifique sua conexão com a Internet")) {
                    textViewDetails.text =
                        "Sem conexão com a Internet. Por favor, verifique sua conexão e tente novamente."
                } else {
                    textViewDetails.text = "CEP não encontrado."
                }

                buttonSearch.isEnabled = true
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrBlank()) {
                if (errorMessage.contains("Verifique sua conexão com a Internet")) {
                    textViewDetails.text =
                        "Sem conexão com a Internet. Por favor, verifique sua conexão e tente novamente."
                } else {
                    textViewDetails.text = errorMessage
                }

                buttonSearch.isEnabled = true
            }
        }
    }

    private fun performCepSearch() {
        val cep = editTextCep.text.toString()
        viewModel.fetchCepDetails(cep)
        addToSearchHistory(cep)
    }

    private fun addToSearchHistory(cep: String) {
        Log.d("MainActivity", "CEP adicionado ao histórico: $cep")
        viewModel.viewModelScope.launch {
            viewModel.insertSearchHistory(cep)
        }
    }
}
