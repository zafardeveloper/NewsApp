package com.example.newsapplication.db.article.history

class HistoryRepository(private val historyDao: HistoryDao) {

    suspend fun saveHistory(history: HistoryEntity) {
        val existingUrl = historyDao.getHistoryByUrl(history.url)
        if (existingUrl != null) {
            historyDao.updateTimestamp(history.url, System.currentTimeMillis())
        } else {
            historyDao.insertHistory(history)
        }
    }

    suspend fun insertHistory(history: HistoryEntity) {
        historyDao.insertHistory(history)
    }

    suspend fun getAllHistories(): List<HistoryEntity> {
        return historyDao.getAllHistories()
    }

    suspend fun deleteAllHistories(articles: List<HistoryEntity>) {
        historyDao.deleteAllHistories(articles)
    }

    suspend fun deleteHistory(history: HistoryEntity) {
        historyDao.deleteHistory(history)
    }
}