package com.example.newsapplication.view.home

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
import com.example.newsapplication.model.Article
import com.example.newsapplication.util.Constants
import com.example.newsapplication.util.NetworkConnectionLiveData
import com.example.newsapplication.util.Resource
import com.example.newsapplication.view.home.adapter.HomeAdapter
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import pl.droidsonroids.gif.GifImageView
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
    private lateinit var noConnectionGif: GifImageView

    private lateinit var connectionLiveData: NetworkConnectionLiveData


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        myAdapter = HomeAdapter(this)
        progressBar = binding.progressBar
        noConnectionGif = binding.noConnectionGif
        appBarLayout = binding.appBarLayout
        connectionLiveData = NetworkConnectionLiveData(requireContext())
        appBarLayoutBg()
        Log.d("MyLog", "onCreateView: onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectionLiveData.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                viewModel.getAllBreakingNews(Constants.TOP_NEWS)
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
                    hideNoConnectionGif()
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

    private fun appBarLayoutBg() {
        val whiteColor = resources.getColor(R.color.white)
        appBarLayout.addOnOffsetChangedListener { _, _ ->
            appBarLayout.setBackgroundColor(whiteColor)
        }
    }
}