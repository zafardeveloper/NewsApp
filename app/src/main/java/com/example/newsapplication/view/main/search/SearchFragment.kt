package com.example.newsapplication.view.main.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.common.OnItemClickListener
import com.example.newsapplication.databinding.FragmentSearchBinding
import com.example.newsapplication.model.Article
import com.example.newsapplication.util.Constants.ARTICLE_KEY
import com.example.newsapplication.util.Constants.CURRENT_SEARCH_TEXT
import com.example.newsapplication.util.Constants.SEARCH_QUERY
import com.example.newsapplication.util.NetworkConnectionLiveData
import com.example.newsapplication.util.Resource
import com.example.newsapplication.view.main.search.queryAdapter.SearchQueryAdapter
import com.example.newsapplication.view.search.SearchActivity
import com.example.newsapplication.view.webView.WebViewActivity
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(), OnItemClickListener<Article> {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchQueryAdapter: SearchQueryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchTV: TextView
    private lateinit var cardView: MaterialCardView
    private lateinit var progressBar: ProgressBar
    private lateinit var connectionLiveData: NetworkConnectionLiveData
    private val viewModel: SearchViewModel by activityViewModels()
    private var isConnected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        searchTV = binding.searchTV
        progressBar = binding.progressBar
        recyclerView = binding.searchRV
        cardView = binding.searchCV
        connectionLiveData = NetworkConnectionLiveData(requireContext())

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectionLiveData.observe(viewLifecycleOwner) {
            isConnected = it
        }

        setupRecyclerView()

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

    private fun setupRecyclerView() {
        searchQueryAdapter = SearchQueryAdapter(this)
        recyclerView.apply {
            adapter = searchQueryAdapter
            layoutManager = LinearLayoutManager(requireContext())
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
        val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
            putExtras(bundle)
        }
        startActivity(intent)
    }
}