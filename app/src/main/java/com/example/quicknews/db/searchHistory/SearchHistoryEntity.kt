package com.example.quicknews.db.searchHistory

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "searchHistory")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("searchQuery")
    val searchQuery: String,
    @SerializedName("timeStamp")
    val timeStamp: Long = System.currentTimeMillis(),
    @SerializedName("isSelected")
    var isSelected: Boolean = false
)