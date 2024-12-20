package com.example.quicknews.common

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.quicknews.util.LocaleHelper

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase!!))
    }

}