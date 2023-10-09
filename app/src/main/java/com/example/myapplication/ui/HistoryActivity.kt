package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityHistoryBinding
import com.example.myapplication.model.Database.AppDatabase
import com.example.myapplication.model.Entity.RetrofitClient
import com.example.myapplication.model.dao.SearchHistoryDao
import com.example.myapplication.network.ConnectivityManager
import com.example.myapplication.repository.CepRepositoryImpl
import com.example.myapplication.ui.adapters.HistoryAdapter
import com.example.myapplication.viewmodel.CepViewModel
import com.example.myapplication.viewmodel.CepViewModelFactory
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity(), HistoryAdapter.OnHistoryItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: CepViewModel
    private lateinit var searchHistoryDao: SearchHistoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerViewHistory
        historyAdapter = HistoryAdapter(this)
        historyAdapter.setOnHistoryItemClickListener(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyAdapter

        // Inicialização
        val appDatabase = AppDatabase.getInstance(applicationContext)
        val viaCepService = RetrofitClient.viaCepService
        val repository = CepRepositoryImpl(viaCepService)
        searchHistoryDao = appDatabase.searchHistoryDao()
        val connectivityManager = ConnectivityManager(this)
        val buttonReturn = findViewById<Button>(R.id.buttonReturn)

        buttonReturn.setOnClickListener {
            onBackPressed()
        }

        // Inicialize o ViewModel passando o SearchHistoryDao corretamente
        viewModel = ViewModelProvider(
            this,
            CepViewModelFactory(repository, connectivityManager, searchHistoryDao)
        ).get(CepViewModel::class.java)

        // Obtenha o histórico real do banco de dados dentro de uma coroutine
        viewModel.viewModelScope.launch {
            val searchHistoryList = viewModel.getSearchHistory()
            historyAdapter.submitList(searchHistoryList)
        }
    }

    override fun onHistoryItemClicked(cep: String) {
        Log.d("HistoryActivity", "Item clicado: $cep")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("cepToSearch", cep)
        Log.d("HistoryActivity", "CEP para pesquisa: $cep")
        startActivity(intent)
    }



}
