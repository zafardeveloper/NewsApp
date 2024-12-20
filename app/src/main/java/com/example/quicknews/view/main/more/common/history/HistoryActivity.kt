package com.example.quicknews.view.main.more.common.history

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import com.example.quicknews.R
import com.example.quicknews.common.BaseActivity
import com.example.quicknews.databinding.ActivityHistoryBinding
import com.example.quicknews.util.Constants.MANAGE_HISTORY
import com.example.quicknews.view.main.more.common.history.fragment.history.HistoryFragment
import com.example.quicknews.view.main.more.common.history.fragment.searchHistory.SearchHistoryFragment

class HistoryActivity : BaseActivity() {
    private val binding by lazy {
        ActivityHistoryBinding.inflate(layoutInflater)
    }

    private lateinit var fragmentContainerView: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusNavigationBarColor()
        init()
        val search = intent.getStringExtra(MANAGE_HISTORY)
        when (search) {
            "manageHistory" -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.historyFragmentContainerView, SearchHistoryFragment())
                    .commit()
            }
            else -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.historyFragmentContainerView, HistoryFragment())
                    .commit()
            }
        }
    }

    private fun init() {
        fragmentContainerView = binding.historyFragmentContainerView
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
}