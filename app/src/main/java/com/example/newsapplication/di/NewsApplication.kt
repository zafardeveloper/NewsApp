package com.example.newsapplication.di

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import com.example.newsapplication.util.LocaleHelper
import com.example.newsapplication.util.SharedPreferencesUtils.getLanguageCode
import com.example.newsapplication.util.ThemeHelper
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocaleHelper.setLocale(this)
        ThemeHelper.applyTheme(this)
    }
}