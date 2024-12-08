package com.example.newsapplication.view.main.more.common.profile

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.databinding.ActivityProfileBinding
import com.example.newsapplication.model.profile.ProfileInfoModel
import com.example.newsapplication.view.main.more.common.profile.adapter.ProfileAdapter
import com.example.newsapplication.view.main.more.common.profile.fragment.avatarImage.FullImageFragment
import com.example.newsapplication.view.main.more.common.profile.fragment.profile.ProfileFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import kotlin.math.abs

class ProfileActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

//    private lateinit var toolbar: MaterialToolbar
//    private lateinit var appBar: AppBarLayout
//    private lateinit var avatarImageView: ImageView
//    private lateinit var cardImageView: CardView
//    private lateinit var avatarFrameLayout: FrameLayout
//    private lateinit var recyclerViewET: RecyclerView
//    private lateinit var profileAdapter: ProfileAdapter
//    private lateinit var editTextList: List<ProfileInfoModel>
    private lateinit var fragmentContainerView: FragmentContainerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusNavigationBarColor()
        fragmentContainerView = binding.profileFragmentContainerView

        supportFragmentManager.beginTransaction()
            .add(R.id.profileFragmentContainerView, ProfileFragment())
            .commit()
//        init()
//        setupToolbar()
//        setupRv()
        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.profileFragmentContainerView)
            when (fragment) {
                is ProfileFragment -> setStatusNavigationBarColor()
                is FullImageFragment -> {
                    window.apply {
                        navigationBarColor = ContextCompat.getColor(this@ProfileActivity, R.color.black)
                        statusBarColor = ContextCompat.getColor(this@ProfileActivity, R.color.black)
                    }
                }
            }
        }
    }



//    private fun init() {
//        toolbar = binding.materialToolbar
//        appBar = binding.appBarLayout
//        avatarImageView = binding.avatarImageView
//        cardImageView = binding.cardImageView
//        avatarFrameLayout = binding.avatarFrameLayout
//        recyclerViewET = binding.recyclerViewET
//        profileAdapter = ProfileAdapter()
//        editTextList = listOf(
//            ProfileInfoModel("Birthday", "17.11.1985"),
//            ProfileInfoModel("Gender", "Male"),
//            ProfileInfoModel("Phone", "+16584454545"),
//            ProfileInfoModel("E-mail", "johnmark@gmail.com"),
//        )
//    }
//
//    private fun setupToolbar() {
//        setSupportActionBar(toolbar)
//        toolbar.apply {
//            setNavigationIcon(R.drawable.ic_back)
//            setNavigationOnClickListener {
//                onBackPressedDispatcher.onBackPressed()
//            }
//            setNavigationIconTint(ContextCompat.getColor(this@ProfileActivity, R.color.white))
//        }
//        appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
//            val collapseRange = appBarLayout.totalScrollRange
//            val collapsePercentage = abs(verticalOffset).toFloat() / collapseRange
//            avatarFrameLayout.apply {
//                alpha = 1 - collapsePercentage
//                scaleX = 1 - collapsePercentage * 0.5f
//                scaleY = 1 - collapsePercentage * 0.5f
//            }
//        }
//    }
//
//    private fun setupRv() {
//        recyclerViewET.apply {
//            layoutManager = LinearLayoutManager(this@ProfileActivity)
//            adapter = profileAdapter
//        }
//        profileAdapter.differ.submitList(editTextList)
//    }

    private fun setStatusNavigationBarColor() {
        window.apply {
            navigationBarColor = ContextCompat.getColor(this@ProfileActivity, R.color.white)
            statusBarColor = ContextCompat.getColor(this@ProfileActivity, R.color.blue)
        }
    }
}