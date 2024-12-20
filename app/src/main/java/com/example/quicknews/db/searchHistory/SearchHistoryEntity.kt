package com.example.quicknews.db.searchHistory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searchHistory")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val searchQuery: String,
    val timeStamp: Long = System.currentTimeMillis()
)