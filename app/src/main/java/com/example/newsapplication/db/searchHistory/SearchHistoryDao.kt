package com.example.newsapplication.db.searchHistory

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SearchHistoryDao {
    @Insert
    suspend fun insertSearchHistory(searchHistory: SearchHistoryEntity)

    @Query("SELECT * FROM searchHistory ORDER BY id DESC")
    suspend fun getAllSearchHistory(): List<SearchHistoryEntity>

    @Delete
    suspend fun deleteAllSearchHistory(histories: List<SearchHistoryEntity>)

    @Delete
    suspend fun deleteSearchHistory(searchHistory: SearchHistoryEntity)
}