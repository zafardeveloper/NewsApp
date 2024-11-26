package com.example.newsapplication.view.webView

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.newsapplication.R
import com.example.newsapplication.databinding.ActivityWebViewBinding
import com.example.newsapplication.model.Article
import com.example.newsapplication.util.Constants.ARTICLE_KEY
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar

class WebViewActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityWebViewBinding.inflate(layoutInflater)
    }

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        setupToolbar()
        setStatusNavigationBarColor()

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        progressBar.visibility = View.VISIBLE
        val article = intent.getParcelableExtra<Article>(ARTICLE_KEY)
        article?.let {
            webView.loadUrl(it.url)
            collapsingToolbarLayout.title = it.source?.name
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
                super.onPageFinished(view, url)
            }
        }
    }

    private fun init() {
        webView = binding.webView
        progressBar = binding.paginationProgressBar
        toolbar = binding.materialToolbar
        collapsingToolbarLayout = binding.collapsingToolbarLayout
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setStatusNavigationBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }
}