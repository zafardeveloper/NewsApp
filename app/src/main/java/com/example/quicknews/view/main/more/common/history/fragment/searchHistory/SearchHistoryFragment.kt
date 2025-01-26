package com.example.quicknews.view.main.more.common.history.fragment.searchHistory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknews.R
import com.example.quicknews.common.BaseFragment
import com.example.quicknews.databinding.FragmentSearchHistoryBinding
import com.example.quicknews.db.searchHistory.SearchHistoryEntity
import com.example.quicknews.db.searchHistory.SearchHistoryItem
import com.example.quicknews.util.Util.Companion.clickAreaButton
import com.example.quicknews.view.main.more.common.history.HistoryViewModel
import com.example.quicknews.view.main.more.common.history.fragment.searchHistory.adapter.SearchHistoryAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchHistoryFragment : BaseFragment(), SearchHistoryAdapter.Listener {

    private var _binding: FragmentSearchHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by activityViewModels()
    private val searchHistoryViewModel: SearchHistoryViewModel by viewModels()

    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBar: AppBarLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var expandedTitleTV: TextView
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchHistories: List<SearchHistoryEntity>
    private var itemSelectedCount: Int = 0

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
        expandedTitleTV = view.findViewById(R.id.expandedTitleTV)
        setupRv()
        setupToolbar()
        loadSearchHistory()
        handleOnBackPressed()
    }

    private fun init() {
        recyclerView = binding.rvSearchHistory
        searchHistoryAdapter = SearchHistoryAdapter(this) {
            showDelete(it)
        }
        collapsingToolbarLayout = binding.collapsingToolbarLayout
        appBar = binding.appBarLayout
        toolbar = binding.materialToolbar
    }

    private fun setupToolbar() {
        appBar.setExpanded(false)
        toolbar.apply {
            inflateMenu(R.menu.action_menu)
            menu[0].setIcon(R.drawable.ic_trash)
            menu.findItem(R.id.action).setVisible(false)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action -> {
                        showConfirmDeleteDialog(getString(R.string.delete_items_confirmation, itemSelectedCount)) {
                            searchHistoryRepository.deleteByIds(searchHistoryAdapter.itemSelectedList)
                        }
                        true
                    }

                    else -> false
                }
            }
            updateToolbarTitleAndIcon(
                getString(R.string.search_history),
                R.drawable.ic_back
            ) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun loadSearchHistory() {
        lifecycleScope.launch {
            searchHistories = withContext(Dispatchers.IO) {
                searchHistoryRepository.getAllSearchHistory()
            }
            val groupedHistories = groupByDate(searchHistories)
            searchHistoryAdapter.differ.submitList(groupedHistories)
        }
    }

    private fun setupRv() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchHistoryAdapter
        }
    }


    override fun onClickHistory(searchHistory: SearchHistoryEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            searchHistoryRepository.saveSearchQuery(searchHistory.searchQuery)
        }
        viewModel.setSearchHistory(searchHistory.searchQuery)
    }

    override fun onLongClickHistory(searchHistory: SearchHistoryEntity) {}

    @SuppressLint("SetTextI18n")
    override fun updateCounterListener(count: Int) {
        itemSelectedCount = count
        if (count == 0) {
            updateToolbarTitleAndIcon(
                getString(R.string.search_history),
                R.drawable.ic_back
            ) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        } else {
            updateToolbarTitleAndIcon(
                "$count ${getString(R.string.selected)}",
                R.drawable.ic_close
            ) {
                searchHistoryAdapter.deselectAll()
            }
        }
    }

    private fun groupByDate(searchHistories: List<SearchHistoryEntity>): List<SearchHistoryItem> {
        val groupedItems = mutableListOf<SearchHistoryItem>()
        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        searchHistories.groupBy {
            val date = Date(it.timeStamp)
            dateFormatter.format(date)
        }.forEach { (date, histories) ->
            groupedItems.add(SearchHistoryItem.DateHeader(date))
            groupedItems.addAll(histories.map {
                SearchHistoryItem.History(it)
            })
        }
        return groupedItems
    }

    private fun updateToolbarTitleAndIcon(title: String, iconRes: Int, onClickAction: () -> Unit) {
        collapsingToolbarLayout.title = title
        expandedTitleTV.text = title
        toolbar.apply {
            setNavigationIcon(iconRes)
            setNavigationOnClickListener { onClickAction() }
        }
    }


    private fun showDelete(flag: Boolean) {
        toolbar.menu.findItem(R.id.action).setVisible(flag)
    }

    private fun showConfirmDeleteDialog(
        message: String,
        onConfirm: suspend () -> Unit,
    ) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_dialog_delete, null)

        val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
        val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
        val title = dialogView.findViewById<TextView>(R.id.textViewTitle)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setView(dialogView)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        title.text = message
        buttonYes.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    onConfirm()
                }
                resetAdapterState()
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

    private fun resetAdapterState() {
        searchHistoryAdapter.itemSelectedPositions.clear()
        searchHistoryAdapter.itemSelectedList.clear()
        searchHistoryAdapter.isSelectionEnable = false
        showDelete(false)

        updateToolbarTitleAndIcon(
            getString(R.string.search_history),
            R.drawable.ic_back
        ) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (searchHistoryAdapter.isSelectionEnable) {
                        searchHistoryAdapter.deselectAll()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }

            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}