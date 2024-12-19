package com.example.newsapplication.util

import android.content.Context
import android.content.SharedPreferences
import com.example.newsapplication.util.Constants.LANGUAGE_CODE_PREFS
import com.example.newsapplication.util.Constants.LANGUAGE_CODE_VALUE
import com.example.newsapplication.util.Constants.LANGUAGE_POSITION_PREFS
import com.example.newsapplication.util.Constants.LANGUAGE_POSITION_VALUE
import com.example.newsapplication.util.Constants.SYSTEM_MODE
import com.example.newsapplication.util.Constants.THEME_MODE_PREFS
import com.example.newsapplication.util.Constants.THEME_MODE_VALUE

object SharedPreferencesUtils {

    fun getLanguageCode(mContext: Context): String {
        val sharedPreferences: SharedPreferences =
            mContext.getSharedPreferences(LANGUAGE_CODE_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(LANGUAGE_CODE_VALUE, "en") ?: "en"
    }

    fun setLanguageCode(mContext: Context, code: String) {
        val sharedPreferences: SharedPreferences =
            mContext.getSharedPreferences(LANGUAGE_CODE_PREFS, Context.MODE_PRIVATE)
        val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(LANGUAGE_CODE_VALUE, code)
        sharedPreferencesEditor.apply()
    }

    fun getLanguagePosition(mContext: Context): Int {
        val sharedPreferences: SharedPreferences =
            mContext.getSharedPreferences(LANGUAGE_POSITION_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(LANGUAGE_POSITION_VALUE, 0)
    }

    fun setLanguagePosition(mContext: Context, number: Int) {
        val sharedPreferences: SharedPreferences =
            mContext.getSharedPreferences(LANGUAGE_POSITION_PREFS, Context.MODE_PRIVATE)
        val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
        sharedPreferencesEditor.putInt(LANGUAGE_POSITION_VALUE, number)
        sharedPreferencesEditor.apply()
    }

    fun getThemeMode(mContext: Context): String {
        val sharedPreferences: SharedPreferences =
            mContext.getSharedPreferences(THEME_MODE_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(THEME_MODE_VALUE, SYSTEM_MODE) ?: SYSTEM_MODE
    }

    fun setThemeMode(mContext: Context, themeMode: String) {
        val sharedPreferences: SharedPreferences =
            mContext.getSharedPreferences(THEME_MODE_PREFS, Context.MODE_PRIVATE)
        val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(THEME_MODE_VALUE, themeMode)
        sharedPreferencesEditor.apply()
    }
}