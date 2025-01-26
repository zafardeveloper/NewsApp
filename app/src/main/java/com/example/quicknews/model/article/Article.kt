package com.example.quicknews.model.article

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Keep
data class Article(
    @SerializedName("id")
    var id: Int? = 0,
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
    val urlToImage: String? = ""
) : Parcelable