package com.example.newsapplication.view.main

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.newsapplication.R
import com.example.newsapplication.databinding.ActivityMainBinding
import com.example.newsapplication.util.Util.Companion.showBottomNavigationView
import com.example.newsapplication.view.main.categories.CategoriesFragment
import com.example.newsapplication.view.main.home.HomeFragment
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
    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var homeFragment: HomeFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var categoriesFragment: CategoriesFragment
    private lateinit var moreFragment: MoreFragment
    private lateinit var activeFragment: Fragment
    private var containerId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        window.statusBarColor = resources.getColor(R.color.black)
        setStatusNavigationBarColor()
        init()
        setupBottomNavigation()
        setupToolbar()
        addFragments()
    }

    private fun init() {
        containerId = R.id.fragmentContainerView
        homeFragment = HomeFragment()
        categoriesFragment = CategoriesFragment()
        searchFragment = SearchFragment()
        moreFragment = MoreFragment()
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
                    ResourcesCompat.getFont(this, R.font.poppins_regular),
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
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, categoriesFragment, "2")
            .hide(categoriesFragment)
            .commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, searchFragment, "3")
            .hide(searchFragment)
            .commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, moreFragment, "4")
            .hide(moreFragment)
            .commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, homeFragment, "1")
            .commit()
        activeFragment = homeFragment
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeFragment -> {
                supportFragmentManager.beginTransaction().hide(activeFragment).show(homeFragment)
                    .commit()
                activeFragment = homeFragment
                showBottomNavigationView(bottomNavigationView)
                supportActionBar?.title = getString(R.string.homeFragment)
            }

            R.id.categoriesFragment -> {
                supportFragmentManager.beginTransaction().hide(activeFragment)
                    .show(categoriesFragment)
                    .commit()
                activeFragment = categoriesFragment
                showBottomNavigationView(bottomNavigationView)
                supportActionBar?.title = getString(R.string.categoriesFragment)
            }

            R.id.searchFragment -> {
                supportFragmentManager.beginTransaction().hide(activeFragment).show(searchFragment)
                    .commit()
                activeFragment = searchFragment
                supportActionBar?.title = getString(R.string.searchFragment)
//                showKeyboardForSearchFragment()
            }

            R.id.moreFragment -> {
                supportFragmentManager.beginTransaction().hide(activeFragment).show(moreFragment)
                    .commit()
                activeFragment = moreFragment
                showBottomNavigationView(bottomNavigationView)
                supportActionBar?.title = getString(R.string.moreFragment)
            }
        }
        return true
    }

    private fun showKeyboardForSearchFragment() {
        val searchEditText = searchFragment.view?.findViewById<EditText>(R.id.searchET)
        searchEditText?.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (bottomNavigationView.selectedItemId != R.id.homeFragment) {
            bottomNavigationView.menu[0].isChecked = true
            onNavigationItemSelected(bottomNavigationView.menu[0])
            return
        } else {
            super.onBackPressed()
        }
    }
}