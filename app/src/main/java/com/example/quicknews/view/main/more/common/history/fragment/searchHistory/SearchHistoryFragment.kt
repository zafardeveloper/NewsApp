package com.example.quicknews.view.main.more.common.history.fragment.searchHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknews.R
import com.example.quicknews.databinding.FragmentSearchHistoryBinding
import com.example.quicknews.db.AppDatabase
import com.example.quicknews.db.searchHistory.SearchHistoryDao
import com.example.quicknews.db.searchHistory.SearchHistoryEntity
import com.example.quicknews.db.searchHistory.SearchHistoryRepository
import com.example.quicknews.util.Util.Companion.clickAreaButton
import com.example.quicknews.view.main.more.common.history.fragment.searchHistory.adapter.SearchHistoryAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchHistoryFragment : Fragment(), SearchHistoryAdapter.Listener {

    private var _binding: FragmentSearchHistoryBinding? = null
    private val binding get () = _binding!!

    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var appBar: AppBarLayout
    private lateinit var searchHistoryDatabase: AppDatabase
    private lateinit var searchHistoryRepository: SearchHistoryRepository
    private lateinit var searchHistoryDao: SearchHistoryDao
    private lateinit var searches: List<SearchHistoryEntity>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchHistoryBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRv()
        loadSearchHistory()
    }

    private fun init() {
        toolbar = binding.materialToolbar
        appBar = binding.appBarLayout
        recyclerView = binding.rvSearchHistory
        searchHistoryAdapter = SearchHistoryAdapter(this)
        searchHistoryDatabase = AppDatabase.getDatabase(requireContext())
        searchHistoryDao = searchHistoryDatabase.searchHistoryDao()
        searchHistoryRepository = SearchHistoryRepository(searchHistoryDao)
    }

    private fun setupToolbar() {
        appBar.setExpanded(false)
        toolbar.apply {
            inflateMenu(R.menu.action_menu)
            menu[0].setIcon(R.drawable.ic_trash)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action -> {
                        showConfirmDeleteDialog(context.getString(R.string.are_you_sure_you_want_to_delete_all_items)) {
                            searchHistoryRepository.deleteAllSearchHistory(searches)
                        }
                        true
                    }

                    else -> false
                }
            }
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            setNavigationIconTint(ContextCompat.getColor(requireContext(), R.color.primary_color))
        }
    }

    private fun loadSearchHistory() {
        lifecycleScope.launch {
            searches = withContext(Dispatchers.IO) {
                searchHistoryRepository.getAllSearchHistory()
            }
            searchHistoryAdapter.differ.submitList(searches)
            if (searches.isNotEmpty()) {
                toolbar.menu.findItem(R.id.action).setVisible(true)
            } else {
                toolbar.menu.findItem(R.id.action).setVisible(false)
            }
        }

    }

    private fun setupRv() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchHistoryAdapter
        }
    }


    override fun onClickHistory(searchHistory: SearchHistoryEntity) {}

    override fun onLongClickHistory(searchHistory: SearchHistoryEntity) {
        showConfirmDeleteDialog(getString(R.string.are_you_sure_you_want_to_delete_this_item)) {
            searchHistoryRepository.deleteSearchHistory(searchHistory)
        }
    }

    private fun showConfirmDeleteDialog(
        message: String,
        onConfirm: suspend () -> Unit
    ) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_dialog_delete, null)

        val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
        val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
        val title = dialogView.findViewById<TextView>(R.id.textViewTitle)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setView(dialogView)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        title.text = message
        buttonYes.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    onConfirm()
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
        alertDialog.window?.setLayout(900, 1200)
    }
}