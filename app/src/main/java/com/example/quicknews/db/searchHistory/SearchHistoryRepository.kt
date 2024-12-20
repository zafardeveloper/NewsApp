package com.example.quicknews.db.searchHistory

class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {

    suspend fun saveSearchQuery(query: String) {
        val existingHistory = searchHistoryDao.getSearchHistoryByQuery(query)
        if (existingHistory != null) {
            searchHistoryDao.updateTimestamp(query, System.currentTimeMillis())
        } else {
            searchHistoryDao.insertSearchHistory(SearchHistoryEntity(searchQuery = query))
        }
    }

    suspend fun getAllSearchHistory(): List<SearchHistoryEntity> =
        searchHistoryDao.getAllSearchHistory()

    suspend fun getPartOfSearchHistory(): List<SearchHistoryEntity> =
        searchHistoryDao.getPartOfSearchHistory()

    suspend fun deleteAllSearchHistory(histories: List<SearchHistoryEntity>) =
        searchHistoryDao.deleteAllSearchHistory(histories)

    suspend fun deleteSearchHistory(searchHistory: SearchHistoryEntity) =
        searchHistoryDao.deleteSearchHistory(searchHistory)

}