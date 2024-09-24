package com.example.newsapplication.view.home.moreHeadlines

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentHeadlinesBinding
import com.example.newsapplication.model.Article
import com.example.newsapplication.util.Constants
import com.example.newsapplication.util.Resource
import com.example.newsapplication.view.home.HomeViewModel
import com.example.newsapplication.view.search.queryAdapter.SearchQueryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HeadlinesFragment : Fragment(), SearchQueryAdapter.Listener {

    private var _binding: FragmentHeadlinesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: SearchQueryAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHeadlinesBinding.inflate(inflater, container, false)
        recyclerView = binding.rvHeadlines
        myAdapter = SearchQueryAdapter(this)
        progressBar = binding.progressBar
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.getAllBreakingNewsHorizontal(Constants.TOP_NEWS_HORIZONTAL)
        setupRv()
        observeViewModel()
    }

    override fun onClick(item: Article) {
        val action = HeadlinesFragmentDirections.actionHeadlinesFragmentToWebViewFragment(item)
        findNavController().navigate(action)
    }

    private fun setupRv() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = myAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.breakingNewsHorizontal.observe(viewLifecycleOwner) { response ->
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

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }
}