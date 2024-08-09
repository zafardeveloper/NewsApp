package com.example.newsapplication.view.favourites

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabbedFragmentAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = mutableListOf<Fragment>()
    private val fragmentTitles = mutableListOf<String>()

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentTitles.add(title)
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getTabTitle(position: Int): String {
        return fragmentTitles[position]
    }

    fun getFragment(position: Int): Fragment {
        return fragments[position]
    }
}