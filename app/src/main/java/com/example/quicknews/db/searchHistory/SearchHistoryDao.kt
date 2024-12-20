package com.example.quicknews.db.searchHistory

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM searchHistory WHERE searchQuery = :query LIMIT 1")
    suspend fun getSearchHistoryByQuery(query: String): SearchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(searchHistory: SearchHistoryEntity)

    @Query("UPDATE searchHistory SET timestamp = :timestamp WHERE searchQuery = :query")
    suspend fun updateTimestamp(query: String, timestamp: Long)

    @Query("SELECT * FROM searchHistory ORDER BY timeStamp DESC")
    suspend fun getAllSearchHistory(): List<SearchHistoryEntity>

    @Query("SELECT * FROM searchHistory ORDER BY timeStamp DESC LIMIT 20")
    suspend fun getPartOfSearchHistory(): List<SearchHistoryEntity>

    @Delete
    suspend fun deleteAllSearchHistory(histories: List<SearchHistoryEntity>)

    @Delete
    suspend fun deleteSearchHistory(searchHistory: SearchHistoryEntity)
}