package com.example.newsapplication.view.categories.singleFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapplication.model.NewsResponse
import com.example.newsapplication.repository.NewsRepository
import com.example.newsapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SingleViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {

    private val _breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val breakingNews: LiveData<Resource<NewsResponse>> get() = _breakingNews

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

    fun getNews(domains: String, q: String) {
        viewModelScope.launch {
            try {
                _breakingNews.postValue(Resource.Loading())
                val response = newsRepository.getNews(domains, q)
                _breakingNews.postValue(handleBrakingNewsResponse(response))
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