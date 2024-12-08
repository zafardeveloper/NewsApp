package com.example.newsapplication.view.main.categories.tabFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentTabBinding
import com.example.newsapplication.db.AppDatabase
import com.example.newsapplication.db.article.readLater.ReadLaterDao
import com.example.newsapplication.db.article.readLater.ReadLaterEntity
import com.example.newsapplication.db.article.readLater.ReadLaterRepository
import com.example.newsapplication.model.article.Article
import com.example.newsapplication.util.Constants
import com.example.newsapplication.util.Constants.ARTICLE_KEY
import com.example.newsapplication.util.Resource
import com.example.newsapplication.util.Util.Companion.showIconPopupMenu
import com.example.newsapplication.view.main.more.common.readLater.ReadLaterActivity
import com.example.newsapplication.view.webView.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TabFragment : Fragment(), TabAdapter.Listener {

    private var _binding: FragmentTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TabViewModel by viewModels()
    private lateinit var myAdapter: TabAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var articleDatabase: AppDatabase
    private lateinit var readLaterRepository: ReadLaterRepository
    private lateinit var readLaterDao: ReadLaterDao


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
        init()
        return binding.root
    }

    private fun init() {
        myAdapter = TabAdapter(this)
        recyclerView = binding.rvBreakingNews
        progressBar = binding.progressBar
        articleDatabase = AppDatabase.getDatabase(requireContext())
        readLaterDao = articleDatabase.articleDao()
        readLaterRepository = ReadLaterRepository(readLaterDao)
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