package com.example.quicknews.view.main.categories.tabFragment

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
import com.example.quicknews.R
import com.example.quicknews.databinding.FragmentTabBinding
import com.example.quicknews.db.AppDatabase
import com.example.quicknews.db.article.readLater.ReadLaterDao
import com.example.quicknews.db.article.readLater.ReadLaterEntity
import com.example.quicknews.db.article.readLater.ReadLaterRepository
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.Constants
import com.example.quicknews.util.Constants.ARTICLE_KEY
import com.example.quicknews.util.Resource
import com.example.quicknews.util.SharedPreferencesUtils.getLanguageCode
import com.example.quicknews.util.Util.Companion.showIconPopupMenu
import com.example.quicknews.view.main.more.common.readLater.ReadLaterActivity
import com.example.quicknews.view.webView.WebViewActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var fab: FloatingActionButton
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
        fab = binding.upFloatingActionButton
        articleDatabase = AppDatabase.getDatabase(requireContext())
        readLaterDao = articleDatabase.articleDao()
        readLaterRepository = ReadLaterRepository(readLaterDao)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myAdapter = TabAdapter(this)
        setupRecyclerView()
        observeViewModel()
        fab.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }
        val data = arguments?.getString(Constants.CATEGORY_KEY)
        val countryCode = when (getLanguageCode(requireContext())) {
            "en" -> {
                getString(R.string.america)
            }
            "ru" -> {
                getString(R.string.russia)
            }
            else -> ""
        }
        when (data) {

            getString(R.string.local) -> {
                viewModel.getLocalNews(countryCode)
            }

            getString(R.string.politics) -> {
                viewModel.getNews(Constants.POLITICS, "politics")
            }

            getString(R.string.economy) -> {
                viewModel.getNews(Constants.ECONOMY, "economy")
            }

            getString(R.string.technologies) -> {
                viewModel.getNews(Constants.TECHNOLOGIES, "technologies")
            }

            getString(R.string.sport) -> {
                viewModel.getNews(Constants.SPORT, "sport")
            }

            getString(R.string.culture) -> {
                viewModel.getNews(Constants.CULTURE, "culture")
            }

            getString(R.string.health) -> {
                viewModel.getNews(Constants.HEALTH, "health")
            }

            getString(R.string.travel) -> {
                viewModel.getNews(Constants.TRAVEL, "travel")
            }

            getString(R.string.science) -> {
                viewModel.getNews(Constants.SCIENCE, "science")
            }

            getString(R.string.cars) -> {
                viewModel.getNews(Constants.CARS, "cars")
            }

            getString(R.string.society) -> {
                viewModel.getNews(Constants.SOCIETY, "society")
            }

            getString(R.string.entertainment) -> {
                viewModel.getNews(Constants.ENTERTAINMENT, "entertainment")
            }

            getString(R.string.incidents) -> {
                viewModel.getNews(Constants.INCIDENTS, "incidents")
            }

            getString(R.string.fashion) -> {
                viewModel.getNews(Constants.FASHION, "fashion")
            }

            getString(R.string.weather) -> {
                viewModel.getNews(Constants.WEATHER, "weather")
            }

            getString(R.string.education) -> {
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

    private fun setupRecyclerView() {
        recyclerView = binding.rvBreakingNews
        recyclerView.apply {
            adapter = myAdapter
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