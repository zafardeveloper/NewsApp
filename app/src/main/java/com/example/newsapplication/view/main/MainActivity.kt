package com.example.newsapplication.view.main

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.newsapplication.R
import com.example.newsapplication.databinding.ActivityMainBinding
import com.example.newsapplication.util.Constants.SEARCH_QUERY
import com.example.newsapplication.util.Constants.SEARCH_REQUEST_CODE
import com.example.newsapplication.view.main.categories.CategoriesFragment
import com.example.newsapplication.view.main.home.HomeFragment
import com.example.newsapplication.view.main.home.HomeViewModel
import com.example.newsapplication.view.main.more.MoreFragment
import com.example.newsapplication.view.main.search.SearchFragment
import com.example.newsapplication.view.search.SearchActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var homeFragment: HomeFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var categoriesFragment: CategoriesFragment
    private lateinit var moreFragment: MoreFragment
    private lateinit var activeFragment: Fragment
    private var containerId: Int? = null
    private var isQueryExists = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setStatusNavigationBarColor()
        init()
        setupBottomNavigation()
        setupToolbar()
        addFragments()
        observeHomeViewModel()
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val searchQuery = data?.getStringExtra(SEARCH_QUERY)
            if (!searchQuery.isNullOrEmpty()) {
                searchFragment.updateSearchQuery(searchQuery)
                bottomNavigationView.selectedItemId = R.id.searchFragment
            }
        }
    }


    private fun init() {
        containerId = R.id.fragmentContainerView
        homeFragment = HomeFragment()
        categoriesFragment = CategoriesFragment()
        searchFragment = SearchFragment()
        moreFragment = MoreFragment()
    }

    private fun observeHomeViewModel() {
        homeViewModel.searchQuery.observe(this) {
            searchFragment.updateSearchQuery(it)
            bottomNavigationView.selectedItemId = R.id.searchFragment
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(this)
    }

    private fun setupToolbar() {
        toolbar = binding.materialToolbar
        setSupportActionBar(toolbar)
        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView && view.text == toolbar.title) {
                view.textSize = 26f
                view.setTypeface(
                    ResourcesCompat.getFont(this, R.font.qanelas_medium),
                    Typeface.NORMAL
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

    private fun addFragments() {
        addFragment(categoriesFragment, "2")
        addFragment(searchFragment, "3")
        addFragment(moreFragment, "4")
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, homeFragment, "1")
            .commit()
        activeFragment = homeFragment
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, fragment, tag)
            .hide(fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeFragment -> {
                switchFragment(homeFragment, getString(R.string.homeFragment))
            }

            R.id.categoriesFragment -> {
                switchFragment(categoriesFragment, getString(R.string.categoriesFragment))
            }

            R.id.searchFragment -> {
                isQueryExists = searchFragment.checkQueryExist()
                if (isQueryExists) {
                    switchFragment(searchFragment, getString(R.string.searchFragment))
                } else {
                    val intent = Intent(this, SearchActivity::class.java)
                    startActivityForResult(intent, SEARCH_REQUEST_CODE)
                    return false
                }
            }

            R.id.moreFragment -> {
                switchFragment(moreFragment, getString(R.string.moreFragment))
            }
        }
        return true
    }

    private fun switchFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction().hide(activeFragment).show(fragment)
            .commit()
        activeFragment = fragment
        supportActionBar?.title = title
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (bottomNavigationView.selectedItemId != R.id.homeFragment) {
            bottomNavigationView.selectedItemId = R.id.homeFragment
            return
        } else {
            super.onBackPressed()
        }
    }
}