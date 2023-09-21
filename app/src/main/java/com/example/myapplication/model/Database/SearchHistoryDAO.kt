package com.example.myapplication.model.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.model.Entity.SearchHistory

@Dao
interface SearchHistoryDao {
    @Insert
    suspend fun insert(searchHistory: SearchHistory)

    @Query("SELECT * FROM search_history")
    suspend fun getAllSearchHistory(): List<SearchHistory>
}
