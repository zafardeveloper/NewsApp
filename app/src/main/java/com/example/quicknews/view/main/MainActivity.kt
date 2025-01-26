package com.example.quicknews.view.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.quicknews.R
import com.example.quicknews.common.BaseActivity
import com.example.quicknews.databinding.ActivityMainBinding
import com.example.quicknews.util.Constants.SEARCH_QUERY
import com.example.quicknews.util.Constants.SEARCH_REQUEST_CODE
import com.example.quicknews.view.main.categories.CategoriesFragment
import com.example.quicknews.view.main.home.HomeFragment
import com.example.quicknews.view.main.home.HomeViewModel
import com.example.quicknews.view.main.more.MoreFragment
import com.example.quicknews.view.main.search.SearchFragment
import com.example.quicknews.view.search.SearchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationBarView.OnItemSelectedListener {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var homeFragment: HomeFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var categoriesFragment: CategoriesFragment
    private lateinit var moreFragment: MoreFragment
    private lateinit var activeFragment: Fragment
    private var searchQuery: String? = ""
    private var containerId: Int? = null
    private var isQueryExists = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusNavigationBarColor()
        init()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            addFragments()
        } else {
            restoreFragments(savedInstanceState)
        }

        observeHomeViewModel()
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            searchQuery = data?.getStringExtra(SEARCH_QUERY)
            if (!searchQuery.isNullOrEmpty()) {
                searchFragment.updateSearchQuery(searchQuery!!)
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

    private fun setStatusNavigationBarColor() {
        window.apply {
            navigationBarColor = ContextCompat.getColor(this@MainActivity, R.color.appBar_color)
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

    private fun restoreFragments(savedInstanceState: Bundle) {
        homeFragment = supportFragmentManager.findFragmentByTag("1") as? HomeFragment ?: HomeFragment()
        categoriesFragment = supportFragmentManager.findFragmentByTag("2") as? CategoriesFragment
            ?: CategoriesFragment()
        searchFragment = supportFragmentManager.findFragmentByTag("3") as? SearchFragment
            ?: SearchFragment()
        moreFragment = supportFragmentManager.findFragmentByTag("4") as? MoreFragment ?: MoreFragment()

        val activeTag = savedInstanceState.getString("activeFragmentTag")
        activeFragment = when (activeTag) {
            "1" -> homeFragment
            "2" -> categoriesFragment
            "3" -> searchFragment
            "4" -> moreFragment
            else -> homeFragment
        }

        supportFragmentManager.beginTransaction().apply {
            hide(homeFragment)
            hide(categoriesFragment)
            hide(searchFragment)
            hide(moreFragment)
            show(activeFragment)
        }.commit()

    }

    @Suppress("DEPRECATION")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeFragment -> {
                switchFragment(homeFragment, getString(R.string.homeFragment))
            }

            R.id.categoriesFragment -> {
                switchFragment(categoriesFragment, getString(R.string.categoriesFragment))
            }

            R.id.searchFragment -> {
                if (searchFragment.isAdded && searchFragment.view != null) {
                    isQueryExists = searchFragment.checkQueryExist()
                }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchTV", searchQuery)
        outState.putString("activeFragmentTag", activeFragment.tag)
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