package com.example.quicknews.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quicknews.db.article.ArticleSourceConverter
import com.example.quicknews.db.article.history.HistoryDao
import com.example.quicknews.db.article.history.HistoryEntity
import com.example.quicknews.db.article.readLater.ReadLaterDao
import com.example.quicknews.db.article.readLater.ReadLaterEntity
import com.example.quicknews.db.searchHistory.SearchHistoryDao
import com.example.quicknews.db.searchHistory.SearchHistoryEntity

@Database(
    entities = [SearchHistoryEntity::class, ReadLaterEntity::class, HistoryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(ArticleSourceConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun readLaterDao(): ReadLaterDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "ComponentsDatabase"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}