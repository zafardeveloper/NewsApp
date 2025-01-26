package com.example.quicknews.view.main.more.common.history.fragment.searchHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchHistoryViewModel : ViewModel() {

    private val _selectedItems = MutableLiveData<List<Int>>()
    val selectedItems: LiveData<List<Int>> = _selectedItems

    private val _counter = MutableLiveData<Int>()
    val counter: LiveData<Int> = _counter

    private var _isSelectionEnable = MutableLiveData<Boolean>()
    val isSelectionEnable: LiveData<Boolean> get() = _isSelectionEnable

    fun setSelectionEnable(flag: Boolean) {
        _isSelectionEnable.value = flag
    }

    fun addItemSelected(id: Int) {
        val currentList = _selectedItems.value.orEmpty().toMutableList()
        currentList.add(id)
        _selectedItems.value = currentList
        updateCounter(currentList.size)
    }

    fun removeItemSelected(id: Int) {
        val currentList = _selectedItems.value.orEmpty().toMutableList()
        currentList.remove(id)
        _selectedItems.value = currentList
        updateCounter(currentList.size)
    }

    fun updateCounter(count: Int) {
        _counter.value = count
    }

    fun setSelectedItems(ids: List<Int>) {
        _selectedItems.value = ids
    }

    fun clearSelectedItems() {
        _selectedItems.value = emptyList()
        updateCounter(0)
    }
}