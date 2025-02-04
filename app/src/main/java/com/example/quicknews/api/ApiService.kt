package com.example.quicknews.api

import com.example.quicknews.util.Constants.API_KEY
import com.example.quicknews.model.article.NewsResponse
import com.example.quicknews.util.Constants.LENTA
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
    suspend fun getLocalNews(
        @Query("q")
        searchQuery: String,
        @Query("excludeDomains")
        excludeDomains: String = LENTA,
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

    @GET("v2/everything")
    suspend fun getAllBreakingNewsWithPaging(
        @Query("domains")
        domains: String,
        @Query("apiKey")
        apiKey: String = API_KEY,
        @Query("page")
        page: Int,
        @Query("pageSize")
        pageSize: Int = 20
    ): Response<NewsResponse>
}