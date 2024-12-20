package com.example.quicknews.db.article

import androidx.room.TypeConverter
import com.example.quicknews.model.article.Source
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ArticleSourceConverter {

    @TypeConverter
    fun fromSource(source: Source?): String {
        return Gson().toJson(source)
    }

    @TypeConverter
    fun toSource(sourceString: String): Source {
        val type = object : TypeToken<Source>() {}.type
        return Gson().fromJson(sourceString, type)
    }
}