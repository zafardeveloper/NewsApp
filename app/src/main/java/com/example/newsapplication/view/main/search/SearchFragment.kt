package com.example.newsapplication.view.main.search

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
import com.example.newsapplication.common.OnItemClickListener
import com.example.newsapplication.databinding.FragmentSearchBinding
import com.example.newsapplication.db.AppDatabase
import com.example.newsapplication.db.article.readLater.ReadLaterDao
import com.example.newsapplication.db.article.readLater.ReadLaterRepository
import com.example.newsapplication.db.article.readLater.ReadLaterEntity
import com.example.newsapplication.model.article.Article
import com.example.newsapplication.util.Constants.ARTICLE_KEY
import com.example.newsapplication.util.Constants.CURRENT_SEARCH_TEXT
import com.example.newsapplication.util.Constants.SEARCH_QUERY
import com.example.newsapplication.util.NetworkConnectionLiveData
import com.example.newsapplication.util.Resource
import com.example.newsapplication.util.Util.Companion.showIconPopupMenu
import com.example.newsapplication.view.main.more.common.readLater.ReadLaterActivity
import com.example.newsapplication.view.main.search.queryAdapter.SearchQueryAdapter
import com.example.newsapplication.view.search.SearchActivity
import com.example.newsapplication.view.webView.WebViewActivity
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), OnItemClickListener<Article> {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchQueryAdapter: SearchQueryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchTV: TextView
    private lateinit var cardView: MaterialCardView
    private lateinit var progressBar: ProgressBar
    private lateinit var articleDatabase: AppDatabase
    private lateinit var readLaterRepository: ReadLaterRepository
    private lateinit var readLaterDao: ReadLaterDao
    private lateinit var connectionLiveData: NetworkConnectionLiveData
    private val viewModel: SearchViewModel by activityViewModels()
    private var isConnected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        searchTV = binding.searchTV
        progressBar = binding.progressBar
        recyclerView = binding.searchRV
        cardView = binding.searchCV
        articleDatabase = AppDatabase.getDatabase(requireContext())
        readLaterDao = articleDatabase.articleDao()
        readLaterRepository = ReadLaterRepository(readLaterDao)
        connectionLiveData = NetworkConnectionLiveData(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectionLiveData.observe(viewLifecycleOwner) {
            isConnected = it
        }

        setupRecyclerView()
        observeViewModel()

        val searchResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.getStringExtra(SEARCH_QUERY)
                    searchTV.text = data
                    viewModel.searchForNews(data ?: "")
                }
            }
        cardView.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java).apply {
                putExtra(CURRENT_SEARCH_TEXT, searchTV.text.toString())
            }
            searchResultLauncher.launch(intent)
        }
    }

    private fun setupRecyclerView() {
        searchQueryAdapter = SearchQueryAdapter(this)
        recyclerView.apply {
            adapter = searchQueryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {

                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        searchQueryAdapter.differ.submitList(newsResponse.articles)
                    }

                    hideProgressBar()
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        Log.e("MyLog", "An error occurred: $message")
                    }
                }

                else -> {}

            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchTV.text = query
        viewModel.searchForNews(query)
    }

    fun checkQueryExist(): Boolean {
        return !searchTV.text.isNullOrEmpty()
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
        val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
            putExtras(bundle)
        }
        startActivity(intent)
    }

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
}