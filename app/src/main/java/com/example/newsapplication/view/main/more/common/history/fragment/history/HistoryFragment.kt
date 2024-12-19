package com.example.newsapplication.view.main.more.common.history.fragment.history

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentHistoryBinding
import com.example.newsapplication.db.AppDatabase
import com.example.newsapplication.db.article.history.HistoryDao
import com.example.newsapplication.db.article.history.HistoryEntity
import com.example.newsapplication.db.article.history.HistoryRepository
import com.example.newsapplication.util.Constants.HISTORY_KEY
import com.example.newsapplication.util.Constants.MODEL_TYPE
import com.example.newsapplication.view.main.more.common.history.HistoryAdapter
import com.example.newsapplication.view.main.more.common.history.fragment.searchHistory.SearchHistoryFragment
import com.example.newsapplication.view.webView.WebViewActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment(), HistoryAdapter.Listener {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var appBar: AppBarLayout
    private lateinit var articleDatabase: AppDatabase
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyDao: HistoryDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRv()
        loadHistories()
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(recyclerView)
        }
    }


    private fun init() {
        recyclerView = binding.rvHistory
        historyAdapter = HistoryAdapter(this)
        toolbar = binding.materialToolbar
        appBar = binding.appBarLayout
        articleDatabase = AppDatabase.getDatabase(requireContext())
        historyDao = articleDatabase.historyDao()
        historyRepository = HistoryRepository(historyDao)
    }

    private val itemTouchHelperCallback by lazy {
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            private val deleteIcon by lazy {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
            }
            private val vibrator by lazy {
                requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            private val intrinsicWidth by lazy { deleteIcon?.intrinsicWidth ?: 0 }
            private val intrinsicHeight by lazy { deleteIcon?.intrinsicHeight ?: 0 }
            private val background by lazy {
                ColorDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.delete
                    )
                )
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @SuppressLint("MissingPermission", "NewApi")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val history = historyAdapter.differ.currentList[position]
                deleteHistory(history)
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        100,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )

                Snackbar.make(
                    binding.root,
                    getString(R.string.item_removed_from_read_it_later),
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction(getString(R.string.undo)) {
                        insertHistory(history)
                    }
                    setActionTextColor(Color.LTGRAY)
                    show()
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top

                if (dX > 0) {
                    background.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + dX.toInt(),
                        itemView.bottom
                    )
                } else {
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                }
                background.draw(c)

                val iconMargin = (itemHeight - intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + intrinsicHeight

                if (dX > 0) {
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = iconLeft + intrinsicWidth
                    deleteIcon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                } else {
                    val iconRight = itemView.right - iconMargin
                    val iconLeft = iconRight - intrinsicWidth
                    deleteIcon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                }

                deleteIcon?.draw(c)

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
    }

    private fun setupRv() {
        recyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupToolbar() {
        appBar.setExpanded(false)
        toolbar.apply {
            inflateMenu(R.menu.action_menu)
            menu[0].setIcon(R.drawable.ic_search_history)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.overlay_from_right,
                                R.anim.overlay_to_left,
                                R.anim.overlay_from_left,
                                R.anim.overlay_to_right
                            )
                            .add(R.id.historyFragmentContainerView, SearchHistoryFragment())
                            .hide(this@HistoryFragment)
                            .addToBackStack(null)
                            .commit()
                        true
                    }

                    else -> false
                }
            }
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

        }
    }

    override fun onClickHistory(item: HistoryEntity) {
        val bundle = Bundle().apply {
            putParcelable(HISTORY_KEY, item)
            putString(MODEL_TYPE, "history")
        }
        val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
            putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun loadHistories() {
        lifecycleScope.launch {
            val histories = withContext(Dispatchers.IO) {
                historyRepository.getAllHistories()
            }
            historyAdapter.differ.submitList(histories)
        }
    }

    private fun insertHistory(history: HistoryEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                historyRepository.insertHistory(history)
                delay(200)
                loadHistories()
            }
        }
    }

    private fun deleteHistory(history: HistoryEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                historyRepository.deleteHistory(history)
                delay(200)
                loadHistories()
            }
        }
    }

}