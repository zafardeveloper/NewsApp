package com.example.newsapplication.view.headlines

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.common.BaseActivity
import com.example.newsapplication.databinding.ActivityHeadlinesBinding
import com.example.newsapplication.db.AppDatabase
import com.example.newsapplication.db.article.readLater.ReadLaterDao
import com.example.newsapplication.db.article.readLater.ReadLaterEntity
import com.example.newsapplication.db.article.readLater.ReadLaterRepository
import com.example.newsapplication.model.article.Article
import com.example.newsapplication.util.Constants
import com.example.newsapplication.util.Constants.ARTICLE_KEY
import com.example.newsapplication.util.OnItemClickListener
import com.example.newsapplication.util.Resource
import com.example.newsapplication.util.Util.Companion.showIconPopupMenu
import com.example.newsapplication.view.main.home.HomeViewModel
import com.example.newsapplication.view.main.more.common.readLater.ReadLaterActivity
import com.example.newsapplication.view.main.search.queryAdapter.SearchQueryAdapter
import com.example.newsapplication.view.webView.WebViewActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HeadlinesActivity : BaseActivity(), OnItemClickListener<Article> {

    private val binding by lazy {
        ActivityHeadlinesBinding.inflate(layoutInflater)
    }

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: SearchQueryAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: MaterialToolbar
    private lateinit var appBar: AppBarLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var articleDatabase: AppDatabase
    private lateinit var readLaterRepository: ReadLaterRepository
    private lateinit var readLaterDao: ReadLaterDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        listener()
        setupToolbar()
        setStatusNavigationBarColor()
        viewModel.getAllBreakingNewsHorizontal(Constants.TOP_NEWS_HORIZONTAL)
        observeViewModel()
        setupRv()
    }
    private fun init() {
        recyclerView = binding.rvHeadlines
        myAdapter = SearchQueryAdapter(this)
        progressBar = binding.progressBar
        toolbar = binding.materialToolbar
        appBar = binding.appBarLayout
        fab = binding.upFloatingActionButton
        articleDatabase = AppDatabase.getDatabase(this)
        readLaterDao = articleDatabase.articleDao()
        readLaterRepository = ReadLaterRepository(readLaterDao)
    }

    private fun listener() {
        fab.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
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

    private fun observeViewModel() {
        viewModel.breakingNewsHorizontal.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        if (myAdapter.differ.currentList != newsResponse.articles)
                            myAdapter.differ.submitList(newsResponse.articles)
                        Log.d("MyLog", "differ.submitList: differ.submitList")
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e("Mylog", "An error occurred: $message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun setupRv() {
        recyclerView.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(this@HeadlinesActivity)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy < 0 && fab.visibility != View.VISIBLE) {
                        fab.show()
                    } else if (dy > 0 && fab.visibility == View.VISIBLE) {
                        fab.hide()
                    } else if (!recyclerView.canScrollVertically(-1)) {
                        fab.hide()
                    }
                }
            })
        }
    }

    private fun setupToolbar() {
        appBar.setExpanded(false)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onClick(item: Article) {
        val bundle = Bundle().apply {
            putParcelable(ARTICLE_KEY, item)
        }
        val intent = Intent(this, WebViewActivity::class.java).apply {
            putExtras(bundle)
        }
        startActivity(intent)
    }

    override fun onLongClick(view: View, item: Article) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.popup_menu)
        showIconPopupMenu(popupMenu)
        val readLaterEntity = ReadLaterEntity(
            author = item.author,
            content = item.content,
            description = item.description,
            publishedAt = item.publishedAt,
            source = item.source,
            title = item.title,
            url = item.url,
            urlToImage = item.urlToImage
        )
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.addReadLater -> {
                    lifecycleScope.launch {
                        readLaterRepository.saveArticle(
                            this@HeadlinesActivity, view, binding.root, readLaterEntity
                        ) { snapBarAction() }
                    }
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun snapBarAction() {
        val intent = Intent(this, ReadLaterActivity::class.java)
        startActivity(intent)
    }
}