package com.example.quicknews.db.article.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {

    @Query("SELECT * FROM historyEntity WHERE url = :url LIMIT 1")
    suspend fun getHistoryByUrl(url: String): HistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("UPDATE historyEntity SET timestamp = :timestamp WHERE url = :url")
    suspend fun updateTimestamp(url: String, timestamp: Long)

    @Query("SELECT * FROM historyEntity ORDER BY timeStamp DESC")
    suspend fun getAllHistories(): List<HistoryEntity>

    @Delete
    suspend fun deleteAllHistories(histories: List<HistoryEntity>)

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)
}