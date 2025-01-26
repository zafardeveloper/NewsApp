package com.example.quicknews.view.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.quicknews.model.article.Article
import com.example.quicknews.model.article.NewsResponse
import com.example.quicknews.repository.NewsRepository
import com.example.quicknews.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {

    private val _breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val breakingNews: LiveData<Resource<NewsResponse>> get() = _breakingNews

    private val _breakingNewsShowMore: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val breakingNewsShowMore: LiveData<Resource<NewsResponse>> get() = _breakingNewsShowMore

    private val _searchQuery: MutableLiveData<String> = MutableLiveData()
    val searchQuery: LiveData<String> get() = _searchQuery
    fun setSearchQuery(query: String) {
        _searchQuery.postValue(query)
    }

    fun getAllBreakingNews(domains: String) {
        viewModelScope.launch {
            _breakingNews.postValue(Resource.Loading())
            try {
                val response = newsRepository.getAllBreakingNews(domains)
                _breakingNews.postValue(handleBrakingNewsResponse(response))
            } catch (e: Exception) {
                _breakingNews.postValue(Resource.Error(e.message))
                Log.d("MyLog", "getAllBreakingNews: $e e.message: ${e.message}")
            }
        }
    }

    fun getAllBreakingNewsShowMore(domains: String) {
        viewModelScope.launch {
            _breakingNewsShowMore.postValue(Resource.Loading())
            try {
                val response = newsRepository.getAllBreakingNews(domains)
                _breakingNewsShowMore.postValue(handleBrakingNewsResponse(response))
            } catch (e: Exception) {
                _breakingNewsShowMore.postValue(Resource.Error(e.message))
                Log.d("MyLog", "getAllBreakingNews: $e e.message: ${e.message}")
            }
        }
    }

    fun getAllBreakingNewsWithPaging(domains: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { newsRepository.getAllBreakingNewsWithPaging(domains) }
        ).flow.cachedIn(viewModelScope)
    }

    private fun handleBrakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        return if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                Resource.Success(resultResponse)
            } ?: Resource.Error("Response body is null")
        } else {
            Resource.Error(response.message())
        }
    }
}