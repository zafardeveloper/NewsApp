package com.example.newsapplication.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentHomeBinding
import com.example.newsapplication.models.Article
import com.example.newsapplication.ui.home.adapter.HomeAdapter
import com.example.newsapplication.util.Constants
import com.example.newsapplication.util.Resource
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeAdapter.Listener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var myAdapter: HomeAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var appBarLayout: AppBarLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        progressBar = binding.paginationProgressBar
        appBarLayout = binding.appBarLayout
        appBarLayoutBg()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllBreakingNews(Constants.TOP_NEWS)
        setupRecyclerView()
        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        myAdapter.differ.submitList(newsResponse.articles)
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
        binding.searchET.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    override fun onClick(item: Article) {
        val action = HomeFragmentDirections.actionHomeFragmentToWebViewFragment(item)
        findNavController().navigate(action)
    }

    private fun setupRecyclerView() {
        myAdapter = HomeAdapter(this)
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

    private fun appBarLayoutBg() {
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                // AppBarLayout is collapsed
                appBarLayout.setBackgroundColor(resources.getColor(R.color.white))
            } else if (verticalOffset == 0) {
                // AppBarLayout is expanded
                appBarLayout.setBackgroundColor(resources.getColor(R.color.white))
            } else {
                // AppBarLayout is in the middle of collapsing/expanding
                appBarLayout.setBackgroundColor(resources.getColor(R.color.white))
            }
        })
    }
}