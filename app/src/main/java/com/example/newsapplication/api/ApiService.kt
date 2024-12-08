package com.example.newsapplication.api

import com.example.newsapplication.util.Constants.API_KEY
import com.example.newsapplication.model.article.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 2,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun getAllBreakingNews(
        @Query("domains")
        domains: String,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun getNews(
        @Query("domains")
        domains: String,
        @Query("q")
        q: String,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}