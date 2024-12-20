package com.example.quicknews.di

import android.app.Application
import com.example.quicknews.util.LocaleHelper
import com.example.quicknews.util.ThemeHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuickNews : Application() {
    override fun onCreate() {
        super.onCreate()
        LocaleHelper.setLocale(this)
        ThemeHelper.applyTheme(this)
    }
}