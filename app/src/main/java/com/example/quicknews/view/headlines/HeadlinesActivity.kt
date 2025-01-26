package com.example.quicknews.view.headlines

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknews.R
import com.example.quicknews.common.BaseActivity
import com.example.quicknews.databinding.ActivityHeadlinesBinding
import com.example.quicknews.db.article.readLater.ReadLaterEntity
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.Constants
import com.example.quicknews.util.Constants.ARTICLE_KEY
import com.example.quicknews.util.OnItemClickListener
import com.example.quicknews.util.Util.Companion.showIconPopupMenu
import com.example.quicknews.view.main.home.HomeViewModel
import com.example.quicknews.view.main.more.common.readLater.ReadLaterActivity
import com.example.quicknews.view.webView.WebViewActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HeadlinesActivity : BaseActivity(), OnItemClickListener<Article> {

    private val binding by lazy {
        ActivityHeadlinesBinding.inflate(layoutInflater)
    }

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView

    //    private lateinit var myAdapter: SearchQueryAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var fab: FloatingActionButton
    private lateinit var myAdapter: HeadlinesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        listener()
        setStatusNavigationBarColor()
        observeViewModel()
        setupRv()

        toolbar = findViewById(R.id.materialToolbar)
        setupToolBar(
            toolbar,
            getString(R.string.headlines)
        )
    }

    private fun init() {
        recyclerView = binding.rvHeadlines
//        myAdapter = SearchQueryAdapter(this)
        myAdapter = HeadlinesAdapter(this)
        fab = binding.upFloatingActionButton
    }

    private fun listener() {
        fab.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }
        binding.noConnectionLayout.tryAgain.setOnClickListener {
            observeViewModel()
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

//        viewModel.getAllBreakingNewsShowMore(Constants.TOP_NEWS_SHOW_MORE)
//        myAdapter.showLoading()
        lifecycleScope.launch {
            viewModel.getAllBreakingNewsWithPaging(Constants.TOP_NEWS_SHOW_MORE).collectLatest {
                it.map { article ->
                    Log.d("MyLog", "observeViewModel: $article")
                }
                myAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            myAdapter.loadStateFlow
                .collectLatest { loadState ->
                    when (val refreshState = loadState.source.refresh) {
                        is LoadState.NotLoading -> {
                            binding.progressbar.visibility = View.INVISIBLE
                            Log.d("pagination", "pagination: NotLoading")
                        }
                        is LoadState.Loading -> {
                            binding.progressbar.visibility = View.VISIBLE
                            Log.d("pagination", "pagination: Loading")
                        }
                        is LoadState.Error -> {
                            binding.progressbar.visibility = View.INVISIBLE
                            Log.d("pagination", "pagination: Error - ${refreshState.error.message}")
                        }
                    }
                }
        }



//        viewModel.breakingNewsShowMore.observe(this) { response ->
//            when (response) {
//                is Resource.Success -> {
//                    recyclerView.visibility = View.VISIBLE
//                    binding.noConnectionLayout.root.visibility = View.GONE
//                    response.data?.let { newsResponse ->
//                        if (myAdapter.differ.currentList != newsResponse.articles) {
//                            myAdapter.setData(newsResponse.articles)
//                        }
//                    }
//                }
//
//                is Resource.Error -> {
//                    recyclerView.visibility = View.GONE
//                    binding.noConnectionLayout.root.visibility = View.VISIBLE
//                    response.message?.let { message ->
//                        Log.e("Mylog", "An error occurred: $message")
//                    }
//                }
//
//                is Resource.Loading -> {
//                    recyclerView.visibility = View.VISIBLE
//                    binding.noConnectionLayout.root.visibility = View.GONE
//                    myAdapter.showLoading()
//                }
//            }
//        }
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