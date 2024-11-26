package com.example.newsapplication.view.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.databinding.ActivitySearchBinding
import com.example.newsapplication.db.AppDatabase
import com.example.newsapplication.db.searchHistory.SearchHistoryDao
import com.example.newsapplication.db.searchHistory.SearchHistoryEntity
import com.example.newsapplication.db.searchHistory.SearchHistoryRepository
import com.example.newsapplication.util.Constants.CURRENT_SEARCH_TEXT
import com.example.newsapplication.util.Constants.SEARCH_QUERY
import com.example.newsapplication.util.NetworkConnectionLiveData
import com.example.newsapplication.util.Util.Companion.clickAreaButton
import com.example.newsapplication.view.main.search.historyAdapter.SearchHistoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity(), SearchHistoryAdapter.Listener {

    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchET: EditText
    private lateinit var cleanBtn: ImageView
    private lateinit var searchHistoryDatabase: AppDatabase
    private lateinit var searchHistoryRepository: SearchHistoryRepository
    private lateinit var searchHistoryDao: SearchHistoryDao
    private lateinit var connectionLiveData: NetworkConnectionLiveData
    private var imm: InputMethodManager? = null
    private var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusNavigationBarColor()
        init()
        setupSearchField()
        setupRv()
        loadSearchHistory()
        checkInternet()
        clickAreaButton(cleanBtn)
        searchET.requestFocus()
        imm?.showSoftInput(searchET, InputMethodManager.SHOW_IMPLICIT)

        val currentSearchText = intent.getStringExtra(CURRENT_SEARCH_TEXT) ?: ""
        searchET.setText(currentSearchText)
        searchET.setSelection(currentSearchText.length)
    }

    private fun init() {
        searchHistoryAdapter = SearchHistoryAdapter(this)
        recyclerView = binding.searchRV
        searchET = binding.searchET
        cleanBtn = binding.cleanSearchText
        searchHistoryDatabase = AppDatabase.getDatabase(this)
        searchHistoryDao = searchHistoryDatabase.searchHistoryDao()
        searchHistoryRepository = SearchHistoryRepository(searchHistoryDao)
        connectionLiveData = NetworkConnectionLiveData(this)
    }

    private fun checkInternet() {
        connectionLiveData.observe(this) {
            isConnected = it
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

    private fun updateCleanBtnVisibility() {
        cleanBtn.visibility = if (searchET.text.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setStatusNavigationBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
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
            addTextChangedListener { _ ->
                updateCleanBtnVisibility()
            }
            setOnEditorActionListener { _, actionId, _ ->
                val editable = this.text
                val searchHistory = SearchHistoryEntity(
                    searchQuery = editable.toString().trim()
                )
                if (actionId == EditorInfo.IME_ACTION_SEARCH && editable.isNotEmpty()) {
                    if (isConnected) {
                        val resultIntent = Intent().apply {
                            putExtra(SEARCH_QUERY, editable.toString().trim())
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        lifecycleScope.launch(Dispatchers.IO) {
                            searchHistoryRepository.insertSearchHistory(searchHistory)
                        }
                        finish()
                    }
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
            adapter = searchHistoryAdapter
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
        finish()
    }

    override fun onLongClickHistory(searchHistory: SearchHistoryEntity) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.custom_dialog_delete, null)

        val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
        val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)

        val alertDialog = AlertDialog.Builder(this)
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