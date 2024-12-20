package com.example.quicknews.db.article.readLater

import android.content.Context
import android.graphics.Color
import android.view.View
import com.example.quicknews.R
import com.example.quicknews.util.Util.Companion.showSnackBar

class ReadLaterRepository(private val readLaterDao: ReadLaterDao) {

    suspend fun saveArticle(
        context: Context,
        view: View,
        anchor: View?,
        article: ReadLaterEntity,
        action:() -> Unit
    ) {
        val existingUrl = readLaterDao.getReadLaterByUrl(article.url)
        if (existingUrl != null) {
            readLaterDao.updateTimestamp(article.url, System.currentTimeMillis())
            showSnackBar(
                context,
                view,
                anchor,
                context.getString(R.string.item_added_to_read_it_later),
                Color.WHITE,
                action
            )
        } else {
            readLaterDao.insertArticle(article)
            showSnackBar(
                context,
                view,
                anchor,
                context.getString(R.string.item_added_to_read_it_later),
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