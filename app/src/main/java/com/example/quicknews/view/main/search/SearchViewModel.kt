package com.example.quicknews.view.main.search

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
class SearchViewModel @Inject constructor(private val newsRepository: NewsRepository) :
    ViewModel() {

    private val _searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: LiveData<Resource<NewsResponse>> get() = _searchNews
    private var searchNewsPage = 1

    fun searchForNews(searchQuery: String) = viewModelScope.launch {
        _searchNews.postValue(Resource.Loading())
        try {
            val response = newsRepository.searchForNews(searchQuery, searchNewsPage)
            _searchNews.postValue(handleSearchNewsResponse(response))
        } catch (e: Exception) {
            _searchNews.postValue(Resource.Error(e.message))
            Log.d("MyLog", "searchForNews: $e e.message: ${e.message}")
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        return if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                Resource.Success(resultResponse)
            } ?: Resource.Error("Response body is null")
        } else {
            Resource.Error(response.message())
        }
    }

}