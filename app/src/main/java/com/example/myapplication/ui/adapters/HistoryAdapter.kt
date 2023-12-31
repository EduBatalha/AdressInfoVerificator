package com.example.myapplication.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Entity.SearchHistory
import java.text.SimpleDateFormat
import java.util.*

interface HistoryItemClickListener {
    fun onHistoryItemClicked(cep: String)
}

class HistoryAdapter(private var clickListener: HistoryItemClickListener) :
    ListAdapter<SearchHistory, HistoryAdapter.ViewHolder>(SearchHistoryDiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val queryTextView: TextView = itemView.findViewById(R.id.textViewQuery)
        val timestampTextView: TextView = itemView.findViewById(R.id.textViewTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchHistory = getItem(position)
        holder.queryTextView.text = searchHistory.query

        // Configurar o clique no TextView
        holder.queryTextView.setOnClickListener {
            clickListener.onHistoryItemClicked(searchHistory.query)
        }

        // Formate o timestamp para uma representação legível
        val timestamp = searchHistory.timestamp
        val formattedTimestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(timestamp))

        holder.timestampTextView.text = formattedTimestamp
    }

    interface OnHistoryItemClickListener : HistoryItemClickListener {
        override fun onHistoryItemClicked(cep: String)
    }

    fun setOnHistoryItemClickListener(listener: OnHistoryItemClickListener) {
        this.clickListener = listener
    }
}

class SearchHistoryDiffCallback : DiffUtil.ItemCallback<SearchHistory>() {
    override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
        return oldItem.query == newItem.query
    }

    override fun areContentsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
        return oldItem == newItem
    }
}
