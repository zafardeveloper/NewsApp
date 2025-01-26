package com.example.quicknews.db.searchHistory

sealed class SearchHistoryItem {
    data class History(val entity: SearchHistoryEntity): SearchHistoryItem()
    data class DateHeader(val date: String): SearchHistoryItem()
}