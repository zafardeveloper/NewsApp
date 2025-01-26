package com.example.quicknews.db.article.history

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.quicknews.model.article.Source
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "historyEntity")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("author")
    val author: String? = "",
    @SerializedName("content")
    val content: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("publishedAt")
    val publishedAt: String? = "",
    @SerializedName("source")
    val source: Source?,
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("url")
    val url: String,
    @SerializedName("urlToImage")
    val urlToImage: String? = "",
    @SerializedName("timeStamp")
    val timeStamp: Long = System.currentTimeMillis()

) : Serializable, Parcelable