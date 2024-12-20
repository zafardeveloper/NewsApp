package com.example.quicknews.view.webView

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.quicknews.R
import com.example.quicknews.common.BaseActivity
import com.example.quicknews.databinding.ActivityWebViewBinding
import com.example.quicknews.db.AppDatabase
import com.example.quicknews.db.article.history.HistoryDao
import com.example.quicknews.db.article.history.HistoryEntity
import com.example.quicknews.db.article.history.HistoryRepository
import com.example.quicknews.db.article.readLater.ReadLaterEntity
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.Constants.ARTICLE_KEY
import com.example.quicknews.util.Constants.HISTORY_KEY
import com.example.quicknews.util.Constants.MODEL_TYPE
import com.example.quicknews.util.Constants.READ_LATER_KEY
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class WebViewActivity : BaseActivity() {

    private val binding by lazy {
        ActivityWebViewBinding.inflate(layoutInflater)
    }

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBar: AppBarLayout
    private lateinit var articleDatabase: AppDatabase
    private lateinit var historyDao: HistoryDao
    private lateinit var historyRepository: HistoryRepository
    private lateinit var article: Article
    private lateinit var readLater: ReadLaterEntity
    private lateinit var history: HistoryEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        setupWebView()
        setupToolbar()
        setStatusNavigationBarColor()
        openUrl()
    }

    private fun init() {
        webView = binding.webView
        progressBar = binding.paginationProgressBar
        toolbar = binding.materialToolbar
        collapsingToolbarLayout = binding.collapsingToolbarLayout
        appBar = binding.appBarLayout
        articleDatabase = AppDatabase.getDatabase(this)
        historyDao = articleDatabase.historyDao()
        historyRepository = HistoryRepository(historyDao)
    }

    private fun setupToolbar() {
        appBar.setExpanded(false)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        progressBar.visibility = View.VISIBLE

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
                super.onPageFinished(view, url)
            }
        }
    }


    @Suppress("DEPRECATION")
    private fun openUrl() {
        val modelType = intent.getStringExtra(MODEL_TYPE)
        when (modelType) {
            "readLater" -> {
                readLater = intent.getParcelableExtra(READ_LATER_KEY)!!
                readLater.let {
                    webView.loadUrl(it.url)
                    collapsingToolbarLayout.title = it.source?.name
                }
            }

            "history" -> {
                history = intent.getParcelableExtra(HISTORY_KEY)!!
                history.let {
                    webView.loadUrl(it.url)
                    collapsingToolbarLayout.title = it.source?.name
                }
            }

            else -> {
                article = intent.getParcelableExtra(ARTICLE_KEY)!!
                article.let {
                    webView.loadUrl(it.url)
                    collapsingToolbarLayout.title = it.source?.name
                    saveHistory(it)
                }
            }
        }
    }

    private fun saveHistory(item: Article) {
        val history = HistoryEntity(
            author = item.author,
            content = item.content,
            description = item.description,
            publishedAt = item.publishedAt,
            source = item.source,
            title = item.title,
            url = item.url,
            urlToImage = item.urlToImage
        )

        lifecycleScope.launch {
            historyRepository.saveHistory(history)
        }
    }

    private fun setStatusNavigationBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.item_color_primary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }
}