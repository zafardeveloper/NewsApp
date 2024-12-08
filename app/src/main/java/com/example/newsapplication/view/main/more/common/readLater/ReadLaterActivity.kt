package com.example.newsapplication.view.main.more.common.readLater

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.databinding.ActivityReadLaterBinding
import com.example.newsapplication.db.AppDatabase
import com.example.newsapplication.db.article.readLater.ReadLaterDao
import com.example.newsapplication.db.article.readLater.ReadLaterEntity
import com.example.newsapplication.db.article.readLater.ReadLaterRepository
import com.example.newsapplication.util.Constants.MODEL_TYPE
import com.example.newsapplication.util.Constants.READ_LATER_KEY
import com.example.newsapplication.view.main.more.common.history.HistoryActivity
import com.example.newsapplication.view.main.more.common.history.HistoryAdapter
import com.example.newsapplication.view.webView.WebViewActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReadLaterActivity : AppCompatActivity(), ReadLaterAdapter.Listener {

    private val binding by lazy {
        ActivityReadLaterBinding.inflate(layoutInflater)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var readLaterAdapter: ReadLaterAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var appBar: AppBarLayout
    private lateinit var articleDatabase: AppDatabase
    private lateinit var readLaterRepository: ReadLaterRepository
    private lateinit var readLaterDao: ReadLaterDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusNavigationBarColor()
        init()
        setupToolbar()
        setupRv()
        loadArticles()

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(recyclerView)
        }
    }

    private fun init() {
        recyclerView = binding.rvReadLater
        readLaterAdapter = ReadLaterAdapter(this)
        toolbar = binding.materialToolbar
        appBar = binding.appBarLayout
        articleDatabase = AppDatabase.getDatabase(this)
        readLaterDao = articleDatabase.articleDao()
        readLaterRepository = ReadLaterRepository(readLaterDao)
    }

    private val itemTouchHelperCallback by lazy {
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            private val deleteIcon by lazy {
                ContextCompat.getDrawable(this@ReadLaterActivity, R.drawable.ic_delete)
            }
            private val vibrator by lazy {
                this@ReadLaterActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            private val intrinsicWidth by lazy { deleteIcon?.intrinsicWidth ?: 0 }
            private val intrinsicHeight by lazy { deleteIcon?.intrinsicHeight ?: 0 }
            private val background by lazy {
                ColorDrawable(
                    ContextCompat.getColor(
                        this@ReadLaterActivity,
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
                val article = readLaterAdapter.differ.currentList[position]
                deleteArticle(article)
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))

                Snackbar.make(
                    binding.root,
                    "Item removed from \"Read it later\"",
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction("Undo") {
                        insertArticle(article)
                    }
                    setActionTextColor(Color.LTGRAY)
                    show()
                }
            }

            override fun onChildDraw(
                canvas: Canvas,
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
                background.draw(canvas)

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

                deleteIcon?.draw(canvas)

                super.onChildDraw(
                    canvas,
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


    private fun setStatusNavigationBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }

    private fun setupRv() {
        recyclerView.apply {
            adapter = readLaterAdapter
            layoutManager = LinearLayoutManager(this@ReadLaterActivity)
        }
    }

    private fun setupToolbar() {
        appBar.setExpanded(false)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onClickHistory(item: ReadLaterEntity) {
        val bundle = Bundle().apply {
            putParcelable(READ_LATER_KEY, item)
            putString(MODEL_TYPE, "readLater")
        }
        val intent = Intent(this, WebViewActivity::class.java).apply {
            putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun loadArticles() {
        lifecycleScope.launch {
            val articles = withContext(Dispatchers.IO) {
                readLaterRepository.getAllArticles()
            }
            readLaterAdapter.differ.submitList(articles)
        }
    }

    private fun insertArticle(article: ReadLaterEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                readLaterRepository.insertArticle(article)
                delay(200)
                loadArticles()
            }
        }
    }

    private fun deleteArticle(article: ReadLaterEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                readLaterRepository.deleteArticle(article)
                delay(200)
                loadArticles()
            }
        }
    }
}