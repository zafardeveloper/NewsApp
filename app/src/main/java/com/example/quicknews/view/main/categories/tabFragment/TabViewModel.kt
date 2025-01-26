package com.example.quicknews.view.main.categories.tabFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknews.model.article.NewsResponse
import com.example.quicknews.repository.NewsRepository
import com.example.quicknews.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TabViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {

    private val _breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val breakingNews: LiveData<Resource<NewsResponse>> get() = _breakingNews

    fun getLocalNews(query: String) = viewModelScope.launch {
        _breakingNews.postValue(Resource.Loading())
        try {
            val response = newsRepository.getLocalNews(query)
            _breakingNews.postValue(handleBrakingNewsResponse(response))
        } catch (e: Exception) {
            _breakingNews.postValue(Resource.Error(e.message))
            Log.d("MyLog", "searchForNews: $e e.message: ${e.message}")
        }
    }

    fun getNews(domains: String, q: String) {
        viewModelScope.launch {
            _breakingNews.postValue(Resource.Loading())
            try {
                val response = newsRepository.getNews(domains, q)
                _breakingNews.postValue(handleBrakingNewsResponse(response))
            } catch (e: Exception) {
                _breakingNews.postValue(Resource.Error(e.message))
                Log.d("MyLog", "getNews: $e e.message: ${e.message}")
            }
        }
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