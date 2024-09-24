package com.example.newsapplication.view.search

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.MainActivity
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentSearchBinding
import com.example.newsapplication.db.AppDatabase
import com.example.newsapplication.db.searchHistory.SearchHistoryDao
import com.example.newsapplication.db.searchHistory.SearchHistoryEntity
import com.example.newsapplication.db.searchHistory.SearchHistoryRepository
import com.example.newsapplication.model.Article
import com.example.newsapplication.util.NetworkConnectionLiveData
import com.example.newsapplication.util.Resource
import com.example.newsapplication.util.clickAreaButton
import com.example.newsapplication.util.hideBottomNavigationView
import com.example.newsapplication.view.search.historyAdapter.SearchHistoryAdapter
import com.example.newsapplication.view.search.queryAdapter.SearchQueryAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchQueryAdapter.Listener, SearchHistoryAdapter.Listener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchQueryAdapter: SearchQueryAdapter
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchET: EditText
    private lateinit var cleanBtn: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var resultTV: TextView
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var connectionLiveData: NetworkConnectionLiveData
    private lateinit var searchHistoryDatabase: AppDatabase

    private lateinit var searchHistoryRepository: SearchHistoryRepository
    private lateinit var searchHistoryDao: SearchHistoryDao
    private val viewModel: SearchViewModel by viewModels()


    private var imm: InputMethodManager? = null
    private var isConnected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        searchET = binding.searchET
        cleanBtn = binding.cleanSearchText
        progressBar = binding.progressBar
        resultTV = binding.resultTV
        appBarLayout = binding.appBarLayout
        recyclerView = binding.searchRV
        connectionLiveData = NetworkConnectionLiveData(requireContext())

        searchHistoryDatabase = AppDatabase.getDatabase(requireContext())
        searchHistoryDao = searchHistoryDatabase.searchHistoryDao()
        searchHistoryRepository = SearchHistoryRepository(searchHistoryDao)

        val bottomNavigationView =
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        CoroutineScope(Dispatchers.Main).launch {
            hideBottomNavigationView(bottomNavigationView)
        }
        appBarLayoutBg()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectionLiveData.observe(viewLifecycleOwner) {
            isConnected = it
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("returned_from_webview")
            ?.observe(viewLifecycleOwner) {
                if (it == true) {
                    Log.d("MyLog", "returned_from_webview: true")
                    updateCleanBtnVisibility()
                }
            }

        setupSearchField()
        setupRecyclerView()

        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {

                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        recyclerView.adapter = searchQueryAdapter
                        searchQueryAdapter.differ.submitList(newsResponse.articles)
                    }
                    resultTV.visibility = View.VISIBLE

                    val queryText = searchET.text.toString()
                    val resultText = "Result(s) on request \"$queryText\""
                    val spannableString = SpannableString(resultText)
                    val startIndex = resultText.indexOf(queryText)
                    val endIndex = startIndex + queryText.length
                    spannableString.setSpan(
                        StyleSpan(Typeface.BOLD_ITALIC),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    resultTV.text = spannableString

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

        clickAreaButton(cleanBtn)

    }

    private fun setupSearchField() {
        cleanBtn.visibility = View.GONE
        searchET.requestFocus()
        imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.searchET, InputMethodManager.SHOW_IMPLICIT)

        searchET.apply {
            addTextChangedListener { editable ->
                updateCleanBtnVisibility()
            }
            setOnEditorActionListener { _, actionId, _ ->
                val editable = this.text
                val searchHistory = SearchHistoryEntity(
                    searchQuery = editable.toString().trim()
                )
                if (actionId == EditorInfo.IME_ACTION_SEARCH && editable.isNotEmpty()) {
                    if (isConnected) {
                        viewModel.searchForNews(editable.toString().trim())
                        lifecycleScope.launch(Dispatchers.IO) {
                            searchHistoryRepository.insertSearchHistory(searchHistory)
                        }
                    }
                }
                imm?.hideSoftInputFromWindow(view?.windowToken, 0)
                searchET.clearFocus()
                cleanBtn.visibility = View.GONE
                true
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    updateCleanBtnVisibility()
                    loadSearchHistory()
                    setupRecyclerView()
                    resultTV.visibility = View.GONE
                }
            }
        }

        cleanBtn.setOnClickListener {
            searchET.setText("")
            searchET.requestFocus()
            imm?.showSoftInput(binding.searchET, InputMethodManager.SHOW_IMPLICIT)
            updateCleanBtnVisibility()
        }
    }

    private fun setupRecyclerView() {
        searchQueryAdapter = SearchQueryAdapter(this)
        searchHistoryAdapter = SearchHistoryAdapter(this)
        recyclerView.apply {
            adapter = searchHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun loadSearchHistory() {
        lifecycleScope.launch {
            val searches = withContext(Dispatchers.IO) {
                searchHistoryRepository.getAllSearchHistory()
            }
            searchHistoryAdapter.differ.submitList(searches)
        }
    }

    override fun onClick(item: Article) {
        val action = SearchFragmentDirections.actionSearchFragmentToWebViewFragment(item)
        findNavController().navigate(action)
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    @Suppress("DEPRECATION")
    private fun appBarLayoutBg() {
        val whiteColor = resources.getColor(R.color.white)
        appBarLayout.addOnOffsetChangedListener { _, _ ->
            appBarLayout.setBackgroundColor(whiteColor)
        }
    }

    private fun updateCleanBtnVisibility() {
        cleanBtn.visibility = if (searchET.text.isEmpty()) View.GONE else View.VISIBLE
    }


    override fun onClickHistory(searchHistory: SearchHistoryEntity) {
        searchET.setText(searchHistory.searchQuery)
        if (isConnected) {
            cleanBtn.visibility = View.GONE
            viewModel.searchForNews(searchET.text.toString())
        }
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        searchET.clearFocus()
    }

    override fun onLongClickHistory(searchHistory: SearchHistoryEntity) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_dialog_delete, requireView() as ViewGroup, false)

        val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
        val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setView(dialogView)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        buttonYes.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    searchHistoryRepository.deleteSearchHistory(searchHistory)
                }
                delay(300L)
                loadSearchHistory()
            }

            alertDialog.dismiss()
        }

        buttonNo.setOnClickListener {
            alertDialog.dismiss()
        }
        clickAreaButton(buttonYes)
        clickAreaButton(buttonNo)
        alertDialog.show()
    }
}