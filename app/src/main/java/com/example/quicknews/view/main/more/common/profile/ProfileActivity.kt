package com.example.quicknews.view.main.more.common.profile

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import com.example.quicknews.R
import com.example.quicknews.databinding.ActivityProfileBinding
import com.example.quicknews.util.LocaleHelper
import com.example.quicknews.view.main.more.common.profile.fragment.avatarImage.FullImageFragment
import com.example.quicknews.view.main.more.common.profile.fragment.profile.ProfileFragment

class ProfileActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private lateinit var fragmentContainerView: FragmentContainerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusNavigationBarColor()
        fragmentContainerView = binding.profileFragmentContainerView

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.profileFragmentContainerView, ProfileFragment())
                .commit()
        }
        supportFragmentManager.addOnBackStackChangedListener {
            val fragment =
                supportFragmentManager.findFragmentById(R.id.profileFragmentContainerView)
            when (fragment) {
                is ProfileFragment -> setStatusNavigationBarColor()
                is FullImageFragment -> {
                    window.apply {
                        navigationBarColor =
                            ContextCompat.getColor(this@ProfileActivity, R.color.black)
                        statusBarColor = ContextCompat.getColor(this@ProfileActivity, R.color.black)
                    }
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase!!))
    }

    private fun setStatusNavigationBarColor() {
        window.apply {
            navigationBarColor = ContextCompat.getColor(this@ProfileActivity, R.color.item_color_primary)
            statusBarColor = ContextCompat.getColor(this@ProfileActivity, R.color.profile_color)
        }
    }
}