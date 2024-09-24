package com.example.newsapplication.view.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
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
    private lateinit var headLinesTV : TextView

    private lateinit var connectionLiveData: NetworkConnectionLiveData


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        myAdapter = HomeAdapter(this)
        progressBar = binding.progressBar
        headLinesTV = binding.headlinesTV
        noConnectionGif = binding.noConnectionGif
        appBarLayout = binding.appBarLayout
        connectionLiveData = NetworkConnectionLiveData(requireContext())
        appBarLayoutBg()

        retainInstance = true
        return binding.root
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
//        viewModel.recyclerViewState?.let {
//            recyclerView.layoutManager?.onRestoreInstanceState(it)
//        }
        observeViewModel()
        binding.searchET.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

//    override fun onPause() {
//        super.onPause()
//        viewModel.recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
//    }

    override fun onClick(item: Article) {
        val action = HomeFragmentDirections.actionHomeFragmentToWebViewFragment(item)
        findNavController().navigate(action)
    }

    override fun onShowMoreClick() {
        val action = HomeFragmentDirections.actionHomeFragmentToHeadlinesFragment()
        findNavController().navigate(action)
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
                        Log.d("MyLog", "setArticles")
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