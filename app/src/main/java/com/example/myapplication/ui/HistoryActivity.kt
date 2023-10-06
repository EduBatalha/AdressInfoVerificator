package com.example.myapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class HistoryActivity : AppCompatActivity() {

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
        historyAdapter = HistoryAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyAdapter

        // Inicialização
        val appDatabase = AppDatabase.getInstance(applicationContext)
        val viaCepService = RetrofitClient.viaCepService
        val repository = CepRepositoryImpl(viaCepService)
        val searchHistoryDao = appDatabase.searchHistoryDao()
        val connectivityManager = ConnectivityManager(this)


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
}
