package com.example.newsapplication.models

class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)