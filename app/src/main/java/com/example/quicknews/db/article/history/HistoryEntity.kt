package com.example.quicknews.db.article.history

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.quicknews.model.article.Source
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "historyEntity")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val author: String? = "",
    val content: String? = "",
    val description: String? = "",
    val publishedAt: String? = "",
    val source: Source?,
    val title: String? = "",
    val url: String,
    val urlToImage: String? = "",
    val timeStamp: Long = System.currentTimeMillis()

) : Serializable, Parcelable