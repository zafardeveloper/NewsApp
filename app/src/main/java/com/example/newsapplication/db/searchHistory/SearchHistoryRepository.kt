package com.example.newsapplication.db.searchHistory

class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {

    suspend fun insertSearchHistory(searchHistory: SearchHistoryEntity) =
        searchHistoryDao.insertSearchHistory(searchHistory)

    suspend fun getAllSearchHistory(): List<SearchHistoryEntity> =
        searchHistoryDao.getAllSearchHistory()

    suspend fun deleteAllSearchHistory(histories: List<SearchHistoryEntity>) =
        searchHistoryDao.deleteAllSearchHistory(histories)

    suspend fun deleteSearchHistory(searchHistory: SearchHistoryEntity) =
        searchHistoryDao.deleteSearchHistory(searchHistory)

}