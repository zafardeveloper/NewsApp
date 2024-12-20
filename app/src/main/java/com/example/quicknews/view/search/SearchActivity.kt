package com.example.quicknews.view.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknews.R
import com.example.quicknews.common.BaseActivity
import com.example.quicknews.databinding.ActivitySearchBinding
import com.example.quicknews.db.AppDatabase
import com.example.quicknews.db.searchHistory.SearchHistoryDao
import com.example.quicknews.db.searchHistory.SearchHistoryEntity
import com.example.quicknews.db.searchHistory.SearchHistoryRepository
import com.example.quicknews.util.Constants.CURRENT_SEARCH_TEXT
import com.example.quicknews.util.Constants.MANAGE_HISTORY
import com.example.quicknews.util.Constants.SEARCH_QUERY
import com.example.quicknews.util.Util.Companion.clickAreaButton
import com.example.quicknews.view.main.more.common.history.HistoryActivity
import com.example.quicknews.view.search.historyAdapter.SearchActivityAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : BaseActivity(), SearchActivityAdapter.Listener {

    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private lateinit var searchActivityAdapter: SearchActivityAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchET: EditText
    private lateinit var cleanBtn: ImageView
    private lateinit var searchHistoryDatabase: AppDatabase
    private lateinit var searchHistoryRepository: SearchHistoryRepository
    private lateinit var searchHistoryDao: SearchHistoryDao
    private var imm: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusNavigationBarColor()
        init()
        setupSearchField()
        setupRv()
        loadSearchHistory()
        clickAreaButton(cleanBtn)
        searchET.requestFocus()
        imm?.showSoftInput(searchET, InputMethodManager.SHOW_IMPLICIT)

        val currentSearchText = intent.getStringExtra(CURRENT_SEARCH_TEXT) ?: ""
        searchET.setText(currentSearchText)
        searchET.setSelection(currentSearchText.length)
    }

    private fun init() {
        searchActivityAdapter = SearchActivityAdapter(this)
        recyclerView = binding.searchRV
        searchET = binding.searchET
        cleanBtn = binding.cleanSearchText
        searchHistoryDatabase = AppDatabase.getDatabase(this)
        searchHistoryDao = searchHistoryDatabase.searchHistoryDao()
        searchHistoryRepository = SearchHistoryRepository(searchHistoryDao)
    }

    private fun loadSearchHistory() {
        lifecycleScope.launch {
            val searches = withContext(Dispatchers.IO) {
                searchHistoryRepository.getPartOfSearchHistory()
            }
            searchActivityAdapter.differ.submitList(searches)
        }
    }

    private fun filterSearchHistory(query: Editable?) {
        query?.let { it ->
            if (it.isNotEmpty()) {
                val searchQuery = query.toString().lowercase().trim()
                lifecycleScope.launch {
                    val filteredSearches = withContext(Dispatchers.IO) {
                        searchHistoryRepository.getPartOfSearchHistory().filter {
                            it.searchQuery.contains(searchQuery)
                        }
                    }
                    searchActivityAdapter.differ.submitList(filteredSearches)
                }
            } else {
                loadSearchHistory()
            }
        }
    }

    private fun updateCleanBtnVisibility() {
        cleanBtn.visibility = if (searchET.text.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setStatusNavigationBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.item_color_primary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }

    private fun setupSearchField() {
        cleanBtn.visibility = View.GONE

        searchET.apply {
            addTextChangedListener {
                updateCleanBtnVisibility()
                lifecycleScope.launch {
                    delay(500L)
                    filterSearchHistory(it)
                }
            }
            setOnEditorActionListener { _, actionId, _ ->
                val editable = this.text
                if (actionId == EditorInfo.IME_ACTION_SEARCH && editable.isNotEmpty()) {
                    val resultIntent = Intent().apply {
                        putExtra(SEARCH_QUERY, editable.toString().trim())
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    lifecycleScope.launch(Dispatchers.IO) {
                        searchHistoryRepository.saveSearchQuery(editable.toString().trim())
                    }
                    finish()
                }
                searchET.clearFocus()
                cleanBtn.visibility = View.GONE
                true
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    updateCleanBtnVisibility()
                    loadSearchHistory()
                    setupRv()
                }
            }
        }

        cleanBtn.setOnClickListener {
            searchET.setText("")
            updateCleanBtnVisibility()
            searchET.requestFocus()
            imm?.showSoftInput(searchET, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun setupRv() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchActivityAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        imm.hideSoftInputFromWindow(windowToken, 0)
                    }
                }
            })
        }
    }

    override fun onClickHistory(searchHistory: SearchHistoryEntity) {
        searchET.setText(searchHistory.searchQuery)
        searchET.clearFocus()
        val resultIntent = Intent().apply {
            putExtra(SEARCH_QUERY, searchET.text.toString().trim())
        }
        setResult(Activity.RESULT_OK, resultIntent)
        lifecycleScope.launch(Dispatchers.IO) {
            searchHistoryRepository.saveSearchQuery(searchET.text.toString().trim())
        }
        finish()
    }

    override fun onLongClickHistory(searchHistory: SearchHistoryEntity) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.custom_dialog_delete, null)

        val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
        val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
        val title = dialogView.findViewById<TextView>(R.id.textViewTitle)

        val alertDialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setView(dialogView)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        title.text = getString(R.string.are_you_sure_you_want_to_delete_this_item)
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
        alertDialog.window?.setLayout(800, 1000)
    }

    override fun onManageHistoryClick() {
        val intent = Intent(this, HistoryActivity::class.java)
        intent.putExtra(MANAGE_HISTORY, "manageHistory")
        startActivity(intent)
    }
}