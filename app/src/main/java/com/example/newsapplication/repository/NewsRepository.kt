package com.example.newsapplication.repository

import com.example.newsapplication.api.ApiService
import com.example.newsapplication.model.article.NewsResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return apiService.getBreakingNews(countryCode, pageNumber)
    }

    suspend fun getAllBreakingNews(domains: String): Response<NewsResponse> {
        return apiService.getAllBreakingNews(domains)
    }

    suspend fun getNews(domains: String, q: String): Response<NewsResponse> {
        return apiService.getNews(domains, q)
    }

    suspend fun searchForNews(searchQuery: String, pageNumber: Int): Response<NewsResponse> {
        return apiService.searchForNews(searchQuery, pageNumber)
    }

    suspend fun getLocalNews(searchQuery: String): Response<NewsResponse> {
        return apiService.getLocalNews(searchQuery)
    }
}