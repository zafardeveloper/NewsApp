package com.example.quicknews.view.main.more.common.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryViewModel: ViewModel() {

    private val _searchHistory: MutableLiveData<String> = MutableLiveData()
    val searchHistory: LiveData<String> get() = _searchHistory
    fun setSearchHistory(query: String) {
        _searchHistory.postValue(query)
    }
}