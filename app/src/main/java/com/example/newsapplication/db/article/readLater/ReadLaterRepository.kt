package com.example.newsapplication.db.article.readLater

import android.graphics.Color
import android.view.View
import com.example.newsapplication.util.Util.Companion.showSnackBar

class ReadLaterRepository(private val readLaterDao: ReadLaterDao) {

    suspend fun saveArticle(
        view: View,
        anchor: View?,
        article: ReadLaterEntity,
        action:() -> Unit
    ) {
        val existingUrl = readLaterDao.getReadLaterByUrl(article.url)
        if (existingUrl != null) {
            readLaterDao.updateTimestamp(article.url, System.currentTimeMillis())
            showSnackBar(
                view,
                anchor,
                "Item added to \"Read it later\"",
                Color.WHITE,
                action
            )
        } else {
            readLaterDao.insertArticle(article)
            showSnackBar(
                view,
                anchor,
                "Item added to \"Read it later\"",
                Color.WHITE,
                action
            )
        }
    }

    suspend fun insertArticle(article: ReadLaterEntity) {
        readLaterDao.insertArticle(article)
    }

    suspend fun getAllArticles(): List<ReadLaterEntity> {
        return readLaterDao.getAllArticles()
    }

    suspend fun deleteAllArticles(articles: List<ReadLaterEntity>) {
        readLaterDao.deleteAllArticles(articles)
    }

    suspend fun deleteArticle(article: ReadLaterEntity) {
        readLaterDao.deleteArticle(article)
    }
}