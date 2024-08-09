package com.example.newsapplication.view.search

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
class SearchViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {

    private val _searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: LiveData<Resource<NewsResponse>> get() = _searchNews
    private var searchNewsPage = 1

    fun searchForNews(searchQuery: String) = viewModelScope.launch {
        try {
            _searchNews.postValue(Resource.Loading())
            val response = newsRepository.searchForNews(searchQuery, searchNewsPage)
            _searchNews.postValue(handleSearchNewsResponse(response))
        } catch (e: Exception) {
            Log.d("MyLog", "searchForNews: $e e.message: ${e.message}")
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

}