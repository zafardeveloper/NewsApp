package com.example.quicknews.common

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.quicknews.R
import com.example.quicknews.db.AppDatabase
import com.example.quicknews.db.article.history.HistoryDao
import com.example.quicknews.db.article.history.HistoryRepository
import com.example.quicknews.db.article.readLater.ReadLaterDao
import com.example.quicknews.db.article.readLater.ReadLaterRepository
import com.example.quicknews.db.searchHistory.SearchHistoryDao
import com.example.quicknews.db.searchHistory.SearchHistoryRepository
import com.google.android.material.appbar.MaterialToolbar

abstract class BaseFragment : Fragment() {


    private lateinit var appDatabase: AppDatabase

    private lateinit var searchHistoryDao: SearchHistoryDao
    protected lateinit var searchHistoryRepository: SearchHistoryRepository

    private lateinit var readLaterDao: ReadLaterDao
    protected lateinit var readLaterRepository: ReadLaterRepository

    private lateinit var historyDao: HistoryDao
    protected lateinit var historyRepository: HistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getDatabase(requireContext())
        searchHistoryDao = appDatabase.searchHistoryDao()
        searchHistoryRepository = SearchHistoryRepository(searchHistoryDao)
        readLaterDao = appDatabase.readLaterDao()
        readLaterRepository = ReadLaterRepository(readLaterDao)
        historyDao = appDatabase.historyDao()
        historyRepository = HistoryRepository(historyDao)
    }

    protected fun setupToolBar(
        toolbar: MaterialToolbar,
        title: String,
        menuResId: Int? = null,
        updateMenuIcon: Int? = null,
        onMenuItemClick: ((MenuItem) -> Boolean)? = null
    ) {
        val textView = view?.findViewById<TextView>(R.id.expandedTitleTV)
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
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

}