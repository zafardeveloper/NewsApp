package com.example.newsapplication.view.main.categories.tabFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.databinding.FragmentTabBinding
import com.example.newsapplication.model.Article
import com.example.newsapplication.util.Constants
import com.example.newsapplication.util.Constants.ARTICLE_KEY
import com.example.newsapplication.util.Resource
import com.example.newsapplication.view.webView.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TabFragment : Fragment(), TabAdapter.Listener {

    private var _binding: FragmentTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TabViewModel by viewModels()
    private lateinit var myAdapter: TabAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar


    companion object {
        fun newInstance(data: String): TabFragment {
            val fragment = TabFragment()
            val args = Bundle()
            args.putString(Constants.CATEGORY_KEY, data)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabBinding.inflate(inflater, container, false)
        myAdapter = TabAdapter(this)
        recyclerView = binding.rvBreakingNews
        progressBar = binding.progressBar
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myAdapter = TabAdapter(this)
        setupRecyclerView()
        observeViewModel()

        val data = arguments?.getString(Constants.CATEGORY_KEY)
        when (data) {

            "Politics" -> {
                viewModel.getNews(Constants.POLITICS, "politics")
            }

            "Economy" -> {
                viewModel.getNews(Constants.ECONOMY, "economy")
            }

            "Technologies" -> {
                viewModel.getNews(Constants.TECHNOLOGIES, "technologies")
            }

            "Sport" -> {
                viewModel.getNews(Constants.SPORT, "sport")
            }

            "Culture" -> {
                viewModel.getNews(Constants.CULTURE, "culture")
            }

            "Health" -> {
                viewModel.getNews(Constants.HEALTH, "health")
            }

            "Travel" -> {
                viewModel.getNews(Constants.TRAVEL, "travel")
            }

            "Science" -> {
                viewModel.getNews(Constants.SCIENCE, "science")
            }

            "Cars" -> {
                viewModel.getNews(Constants.CARS, "cars")
            }

            "Society" -> {
                viewModel.getNews(Constants.SOCIETY, "society")
            }

            "Entertainment" -> {
                viewModel.getNews(Constants.ENTERTAINMENT, "entertainment")
            }

            "Incidents" -> {
                viewModel.getNews(Constants.INCIDENTS, "incidents")
            }

            "Fashion" -> {
                viewModel.getNews(Constants.FASHION, "fashion")
            }

            "Weather" -> {
                viewModel.getNews(Constants.WEATHER, "weather")
            }

            "Education" -> {
                viewModel.getNews(Constants.EDUCATION, "education")
            }

        }
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

    override fun onClick(item: Article) {
        val bundle = Bundle().apply {
            putParcelable(ARTICLE_KEY, item)
        }
        val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
            putExtras(bundle)
        }
        startActivity(intent)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}