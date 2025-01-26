package com.example.quicknews.common

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.example.quicknews.R
import com.example.quicknews.db.AppDatabase
import com.example.quicknews.db.article.history.HistoryDao
import com.example.quicknews.db.article.history.HistoryRepository
import com.example.quicknews.db.article.readLater.ReadLaterDao
import com.example.quicknews.db.article.readLater.ReadLaterRepository
import com.example.quicknews.db.searchHistory.SearchHistoryDao
import com.example.quicknews.db.searchHistory.SearchHistoryRepository
import com.example.quicknews.util.Constants.DARK_MODE
import com.example.quicknews.util.Constants.LIGHT_MODE
import com.example.quicknews.util.Constants.SYSTEM_MODE
import com.example.quicknews.util.LocaleHelper
import com.example.quicknews.util.SharedPreferencesUtils
import com.google.android.material.appbar.MaterialToolbar

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase

    private lateinit var searchHistoryDao: SearchHistoryDao
    protected lateinit var searchHistoryRepository: SearchHistoryRepository

    private lateinit var readLaterDao: ReadLaterDao
    protected lateinit var readLaterRepository: ReadLaterRepository

    private lateinit var historyDao: HistoryDao
    protected lateinit var historyRepository: HistoryRepository

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getDatabase(this)
        searchHistoryDao = appDatabase.searchHistoryDao()
        searchHistoryRepository = SearchHistoryRepository(searchHistoryDao)
        readLaterDao = appDatabase.readLaterDao()
        readLaterRepository = ReadLaterRepository(readLaterDao)
        historyDao = appDatabase.historyDao()
        historyRepository = HistoryRepository(historyDao)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        updateStatusBarAppearance()
    }

    private fun updateStatusBarAppearance() {
        val isLightMode =
            when (SharedPreferencesUtils.getThemeMode(this)) {
                LIGHT_MODE -> true
                DARK_MODE -> false
                SYSTEM_MODE -> {
                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO
                }

                else -> false
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isLightMode) {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                window.insetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
        }
    }

    protected fun setupToolBar(
        toolbar: MaterialToolbar,
        title: String,
        menuResId: Int? = null,
        updateMenuIcon: Int? = null,
        onMenuItemClick: ((MenuItem) -> Boolean)? = null
    ) {
        val textView = findViewById<TextView>(R.id.expandedTitleTV)
        textView?.text = title
        toolbar.title = title

        menuResId?.let {
            toolbar.inflateMenu(it)
        }

        updateMenuIcon?.let {
            toolbar.menu[0].setIcon(it)
        }

        toolbar.setOnMenuItemClickListener {
            onMenuItemClick?.invoke(it) ?: false
        }

        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}