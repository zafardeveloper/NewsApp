package com.example.newsapplication.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.newsapplication.util.Constants.DARK_MODE
import com.example.newsapplication.util.Constants.LIGHT_MODE
import com.example.newsapplication.util.Constants.SYSTEM_MODE

object ThemeHelper {

    fun applyTheme(context: Context): Context {
        when (SharedPreferencesUtils.getThemeMode(context)) {
            LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            SYSTEM_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        return context
    }

    fun setThemeMode(context: Context, themeMode: String) {
        SharedPreferencesUtils.setThemeMode(context, themeMode)
        applyTheme(context)
    }

}