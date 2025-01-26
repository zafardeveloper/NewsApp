package com.example.quicknews.view.main.more.common.history.fragment.history

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknews.R
import com.example.quicknews.common.BaseFragment
import com.example.quicknews.databinding.FragmentHistoryBinding
import com.example.quicknews.db.article.history.HistoryEntity
import com.example.quicknews.util.Constants.HISTORY_KEY
import com.example.quicknews.util.Constants.MODEL_TYPE
import com.example.quicknews.util.OnItemClickListener
import com.example.quicknews.view.main.more.common.history.HistoryAdapter
import com.example.quicknews.view.main.more.common.history.fragment.searchHistory.SearchHistoryFragment
import com.example.quicknews.view.webView.WebViewActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : BaseFragment(), OnItemClickListener<HistoryEntity> {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var appBar: AppBarLayout

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
        setupRv()
        loadHistories()
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(recyclerView)
        }

        toolbar = view.findViewById(R.id.materialToolbar)
        appBar = view.findViewById(R.id.appBarLayout)
        setupToolBar(
            toolbar = toolbar,
            title = getString(R.string.history),
            menuResId = R.menu.action_menu,
            updateMenuIcon = R.drawable.ic_search_history
        ) {
            when (it.itemId) {
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

    }


    private fun init() {
        recyclerView = binding.rvHistory
        historyAdapter = HistoryAdapter(this)
    }

    @Suppress("DEPRECATION")
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
                    setBackgroundTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.dark_gray_95
                        )
                    )
                    setActionTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.item_color_primary
                        )
                    )
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

    override fun onClick(item: HistoryEntity) {
        val bundle = Bundle().apply {
            putParcelable(HISTORY_KEY, item)
            putString(MODEL_TYPE, "history")
        }
        val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
            putExtras(bundle)
        }
        startActivity(intent)
    }

    override fun onLongClick(view: View, item: HistoryEntity) {}

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}