package com.example.newsapplication.view.main.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentHomeBinding
import com.example.newsapplication.db.AppDatabase
import com.example.newsapplication.db.article.readLater.ReadLaterDao
import com.example.newsapplication.db.article.readLater.ReadLaterEntity
import com.example.newsapplication.db.article.readLater.ReadLaterRepository
import com.example.newsapplication.model.article.Article
import com.example.newsapplication.util.Constants
import com.example.newsapplication.util.Constants.ARTICLE_KEY
import com.example.newsapplication.util.Constants.SEARCH_QUERY
import com.example.newsapplication.util.NetworkConnectionLiveData
import com.example.newsapplication.util.Resource
import com.example.newsapplication.util.Util.Companion.showIconPopupMenu
import com.example.newsapplication.view.headlines.HeadlinesActivity
import com.example.newsapplication.view.main.home.adapter.HomeAdapter
import com.example.newsapplication.view.main.more.common.readLater.ReadLaterActivity
import com.example.newsapplication.view.search.SearchActivity
import com.example.newsapplication.view.webView.WebViewActivity
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeAdapter.Listener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var myAdapter: HomeAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var noConnectionGif: GifImageView
    private lateinit var headLinesTV: TextView
    private lateinit var articleDatabase: AppDatabase
    private lateinit var readLaterRepository: ReadLaterRepository
    private lateinit var readLaterDao: ReadLaterDao

    private lateinit var connectionLiveData: NetworkConnectionLiveData


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()
        appBarLayoutBg()
        return binding.root
    }

    private fun init() {
        myAdapter = HomeAdapter(this)
        progressBar = binding.progressBar
        headLinesTV = binding.headlinesTV
        noConnectionGif = binding.noConnectionGif
        appBarLayout = binding.appBarLayout
        articleDatabase = AppDatabase.getDatabase(requireContext())
        readLaterDao = articleDatabase.articleDao()
        readLaterRepository = ReadLaterRepository(readLaterDao)
        connectionLiveData = NetworkConnectionLiveData(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectionLiveData.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected && savedInstanceState == null) {
                viewModel.getAllBreakingNews(Constants.TOP_NEWS)
                viewModel.getAllBreakingNewsHorizontal(Constants.TOP_NEWS_HORIZONTAL)
            }
        }
        try {
            if (!connectionLiveData.isConnected() && myAdapter.differ.currentList.size == 0) {
                showNoConnectionGif()
            }
        } catch (e: Exception) {
            Log.d("MyLog", "onViewCreated: ${e.message}", e)
        }
        setupRecyclerView()
        observeViewModel()


        val searchResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.getStringExtra(SEARCH_QUERY)
                    data?.let {
                        viewModel.setSearchQuery(data)
                    }
                }
            }

        binding.searchTV.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            searchResultLauncher.launch(intent)
        }
    }

    override fun onClick(item: Article) {
        val bundle = Bundle().apply {
            putParcelable(ARTICLE_KEY, item)
        }
        val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
            putExtras(bundle)
        }
        startActivity(intent)
    }


    override fun onShowMoreClick() {
        val intent = Intent(requireContext(), HeadlinesActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("ShowToast")
    override fun onLongClick(view: View, item: Article) {
        val popupMenu = PopupMenu(requireContext(), view)
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
                            view,
                            requireActivity().findViewById(R.id.bottomNavigationView),
                            readLaterEntity
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
        val intent = Intent(requireContext(), ReadLaterActivity::class.java)
        startActivity(intent)
    }

    private fun observeViewModel() {
        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        if (myAdapter.differ.currentList != newsResponse.articles)
                            myAdapter.differ.submitList(newsResponse.articles)
                    }
                    headLinesTV.visibility = View.VISIBLE
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e("Mylog", "An error occurred: $message")
                    }
                }

                is Resource.Loading -> {
                    hideNoConnectionGif()
                    showProgressBar()
                }
            }
        }

        viewModel.breakingNewsHorizontal.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        if (myAdapter.horizontalArticles != newsResponse.articles)
                            myAdapter.setArticles(newsResponse.articles)
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e("Mylog", "An error occurred: $message")
                    }
                }

                is Resource.Loading -> {
                    hideNoConnectionGif()
                    showProgressBar()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView = binding.rvBreakingNews
        recyclerView.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideNoConnectionGif() {
        noConnectionGif.visibility = View.GONE
    }

    private fun showNoConnectionGif() {
        noConnectionGif.visibility = View.VISIBLE
    }

    @Suppress("DEPRECATION")
    private fun appBarLayoutBg() {
        val whiteColor = resources.getColor(R.color.white)
        appBarLayout.addOnOffsetChangedListener { _, _ ->
            appBarLayout.setBackgroundColor(whiteColor)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}