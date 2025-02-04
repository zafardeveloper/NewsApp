package com.example.quicknews.view.main.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.quicknews.R
import com.example.quicknews.common.BaseFragment
import com.example.quicknews.databinding.FragmentHomeBinding
import com.example.quicknews.db.article.readLater.ReadLaterEntity
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.Constants
import com.example.quicknews.util.Constants.ARTICLE_KEY
import com.example.quicknews.util.Constants.SEARCH_QUERY
import com.example.quicknews.util.Resource
import com.example.quicknews.util.SessionManager
import com.example.quicknews.util.Util.Companion.showIconPopupMenu
import com.example.quicknews.view.headlines.HeadlinesActivity
import com.example.quicknews.view.main.home.adapter.HomeAdapter
import com.example.quicknews.view.main.more.common.profile.ProfileActivity
import com.example.quicknews.view.main.more.common.readLater.ReadLaterActivity
import com.example.quicknews.view.search.SearchActivity
import com.example.quicknews.view.webView.WebViewActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment(), HomeAdapter.Listener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var myAdapter: HomeAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var profileImage: ImageView
    private lateinit var searchResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var sessionManager: SessionManager
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        listener()

    }

    private fun init() {
        myAdapter = HomeAdapter(this)
        progressBar = binding.progressBar
        profileImage = binding.profileImage
        sessionManager = SessionManager(requireContext())
        fab = binding.upFloatingActionButton
        searchResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.getStringExtra(SEARCH_QUERY)
                    data?.let {
                        viewModel.setSearchQuery(data)
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        val avatarUri = sessionManager.getAvatarImage()
        if (avatarUri != null) {
            Glide.with(requireContext())
                .load(avatarUri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profileImage)
        } else {
            Glide.with(requireContext()).load(R.drawable.ic_avatar).into(profileImage)
        }
    }

    private fun listener() {
        profileImage.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
        fab.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }
        binding.noConnectionLayout.tryAgain.setOnClickListener {
            observeViewModel()
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


    override fun onShowMoreClick() {
        val intent = Intent(requireContext(), HeadlinesActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("ShowToast")
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

    override fun onSearchClick() {
        val intent = Intent(requireContext(), SearchActivity::class.java)
        searchResultLauncher.launch(intent)
    }

    private fun snapBarAction() {
        val intent = Intent(requireContext(), ReadLaterActivity::class.java)
        startActivity(intent)
    }

    private fun observeViewModel() {

        viewModel.getAllBreakingNews(Constants.TOP_NEWS)
        myAdapter.showLoading()

        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    recyclerView.visibility = View.VISIBLE
                    binding.noConnectionLayout.root.visibility = View.GONE
                    response.data?.let { newsResponse ->
                        myAdapter.setData(newsResponse.articles)
                    }
                }

                is Resource.Error -> {
                    recyclerView.visibility = View.GONE
                    binding.noConnectionLayout.root.visibility = View.VISIBLE
                    response.message?.let { message ->
                        Log.e("Mylog", "An error occurred: $message")
                    }
                }

                is Resource.Loading -> {
                    myAdapter.showLoading()
                    recyclerView.visibility = View.VISIBLE
                    binding.noConnectionLayout.root.visibility = View.GONE
                }
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}