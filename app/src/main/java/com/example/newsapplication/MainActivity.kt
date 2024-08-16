package com.example.newsapplication

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapplication.databinding.ActivityMainBinding
import com.example.newsapplication.util.hideBottomNavigationView
import com.example.newsapplication.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        window.statusBarColor = resources.getColor(R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        val toolbar = binding.materialToolbar

        setSupportActionBar(toolbar)
        val bottomNavigationView = binding.bottomNavigationView
        val navController = findNavController(R.id.fragmentContainerView)
        navController.setGraph(R.navigation.nav_graph)
        bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.webViewFragment -> {
                    lifecycleScope.launch {
                        hideBottomNavigationView(bottomNavigationView)
                    }
                    toolbar.setNavigationIconTint(ContextCompat.getColor(this, R.color.blue))
                    toolbar.setNavigationIcon(R.drawable.ic_back)
                }

                R.id.homeFragment -> {
                    showBottomNavigationView(bottomNavigationView)
                    bottomNavigationView.visibility = View.VISIBLE
                    toolbar.navigationIcon = null
                }

                else -> {
                    showBottomNavigationView(bottomNavigationView)
                    bottomNavigationView.visibility = View.VISIBLE
                    toolbar.setNavigationIconTint(ContextCompat.getColor(this, R.color.blue))
                    toolbar.setNavigationIcon(R.drawable.ic_back)
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return super.onSupportNavigateUp() || navController.navigateUp()
    }


}