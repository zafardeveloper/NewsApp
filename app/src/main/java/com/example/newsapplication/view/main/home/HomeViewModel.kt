package com.example.newsapplication.view.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapplication.model.article.NewsResponse
import com.example.newsapplication.repository.NewsRepository
import com.example.newsapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {

    private val _breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val breakingNews: LiveData<Resource<NewsResponse>> get() = _breakingNews
    private var breakingNewsPage = 1

    private val _breakingNewsHorizontal: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val breakingNewsHorizontal: LiveData<Resource<NewsResponse>> get() = _breakingNewsHorizontal

    private val _searchQuery: MutableLiveData<String> = MutableLiveData()
    val searchQuery: LiveData<String> get() = _searchQuery

    fun setSearchQuery(query: String) {
        _searchQuery.postValue(query)
    }

    init {
//        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        try {
            _breakingNews.postValue(Resource.Loading())
            val response = newsRepository.getBreakingNews(
                countryCode = countryCode,
                pageNumber = breakingNewsPage
            )
            _breakingNews.postValue(handleBrakingNewsResponse(response))
        } catch (e: Exception) {
            Log.d("MyLog", "getBreakingNews: $e e.message: ${e.message}")
        }
    }

    fun getAllBreakingNews(domains: String) {
        viewModelScope.launch {
            try {
                _breakingNews.postValue(Resource.Loading())
                val response = newsRepository.getAllBreakingNews(domains)
                _breakingNews.postValue(handleBrakingNewsResponse(response))
            } catch (e: Exception) {
                Log.d("MyLog", "getAllBreakingNews: $e e.message: ${e.message}")
            }
        }
    }

    fun getAllBreakingNewsHorizontal(domains: String) {
        viewModelScope.launch {
            try {
                _breakingNewsHorizontal.postValue(Resource.Loading())
                val response = newsRepository.getAllBreakingNews(domains)
                _breakingNewsHorizontal.postValue(handleBrakingNewsResponse(response))
            } catch (e: Exception) {
                Log.d("MyLog", "getAllBreakingNews: $e e.message: ${e.message}")
            }
        }
    }



    private fun handleBrakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}