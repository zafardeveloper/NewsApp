package com.example.quicknews.model.article

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class NewsResponse(
    @SerializedName("articles")
    val articles: List<Article>,
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Int
)