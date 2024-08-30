package com.example.newsapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsapplication.db.searchHistory.SearchHistoryDao
import com.example.newsapplication.db.searchHistory.SearchHistoryEntity

@Database(entities = [SearchHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

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