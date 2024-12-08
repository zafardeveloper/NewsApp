package com.example.newsapplication.db.article.readLater

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapplication.model.article.Source
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "readLaterEntity")
data class ReadLaterEntity(
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