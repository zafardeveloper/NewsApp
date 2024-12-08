package com.example.newsapplication.model.article

class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)