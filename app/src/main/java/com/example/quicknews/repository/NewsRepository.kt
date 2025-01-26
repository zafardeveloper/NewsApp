package com.example.quicknews.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.quicknews.api.ApiService
import com.example.quicknews.model.article.Article
import com.example.quicknews.model.article.NewsResponse
import kotlinx.coroutines.delay
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

    fun getAllBreakingNewsWithPaging(domains: String): PagingSource<Int, Article> {
        return object : PagingSource<Int, Article>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
                delay(3000L)
                return try {
                    val currentPage = params.key ?: 1
                    val response = apiService.getAllBreakingNewsWithPaging(
                        domains = domains,
                        page = currentPage,
                        pageSize = params.loadSize
                    )
                    if (response.isSuccessful) {
                        val data = response.body()?.articles ?: emptyList()
                        LoadResult.Page(
                            data = data,
                            prevKey = if (currentPage == 1) null else currentPage - 1,
                            nextKey = if (data.isEmpty()) null else currentPage + 1
                        )
                    } else {
                        LoadResult.Error(Exception(response.message()))
                    }
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
                return state.anchorPosition?.let {
                    state.closestPageToPosition(it)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
                }
            }
        }
    }

}