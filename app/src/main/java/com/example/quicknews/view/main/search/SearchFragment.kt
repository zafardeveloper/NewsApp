package com.example.quicknews.view.main.search

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
import com.example.quicknews.R
import com.example.quicknews.databinding.FragmentSearchBinding
import com.example.quicknews.db.AppDatabase
import com.example.quicknews.db.article.readLater.ReadLaterDao
import com.example.quicknews.db.article.readLater.ReadLaterEntity
import com.example.quicknews.db.article.readLater.ReadLaterRepository
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.Constants.ARTICLE_KEY
import com.example.quicknews.util.Constants.CURRENT_SEARCH_TEXT
import com.example.quicknews.util.Constants.SEARCH_QUERY
import com.example.quicknews.util.OnItemClickListener
import com.example.quicknews.util.Resource
import com.example.quicknews.util.Util.Companion.showIconPopupMenu
import com.example.quicknews.view.main.more.common.readLater.ReadLaterActivity
import com.example.quicknews.view.main.search.queryAdapter.SearchQueryAdapter
import com.example.quicknews.view.search.SearchActivity
import com.example.quicknews.view.webView.WebViewActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var fab: FloatingActionButton
    private val viewModel: SearchViewModel by activityViewModels()

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
        fab = binding.upFloatingActionButton
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        fab.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun setupRecyclerView() {
        searchQueryAdapter = SearchQueryAdapter(this)
        recyclerView.apply {
            adapter = searchQueryAdapter
            layoutManager = LinearLayoutManager(requireContext())
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
                            requireContext(),
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}