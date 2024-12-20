package com.example.quicknews.db.article.readLater

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReadLaterDao {

    @Query("SELECT * FROM readLaterEntity WHERE url = :url LIMIT 1")
    suspend fun getReadLaterByUrl(url: String): ReadLaterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ReadLaterEntity)

    @Query("UPDATE readLaterEntity SET timestamp = :timestamp WHERE url = :url")
    suspend fun updateTimestamp(url: String, timestamp: Long)

    @Query("SELECT * FROM readLaterEntity ORDER BY timeStamp DESC")
    suspend fun getAllArticles(): List<ReadLaterEntity>

    @Delete
    suspend fun deleteAllArticles(articles: List<ReadLaterEntity>)

    @Delete
    suspend fun deleteArticle(article: ReadLaterEntity)
}