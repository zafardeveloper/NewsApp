package com.example.newsapplication.model.article

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Article(
    var id: Int? = 0,
    val author: String ?= "",
    val content: String? = "",
    val description: String? = "",
    val publishedAt: String? = "",
    val source: Source?,
    val title: String? = "",
    val url: String,
    val urlToImage: String? = ""
) : Serializable, Parcelable