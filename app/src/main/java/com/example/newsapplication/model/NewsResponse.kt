package com.example.newsapplication.model

class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)