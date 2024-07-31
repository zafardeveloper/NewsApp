package com.example.newsapplication.ui.favourites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentRootBinding
import com.example.newsapplication.ui.favourites.favorites.FavoriteFragment
import com.example.newsapplication.ui.favourites.history.HistoryFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RootFragment : Fragment() {

    private var _binding: FragmentRootBinding? = null
    private val binding get () = _binding!!
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager2
    lateinit var adapter: TabbedFragmentAdapter
    lateinit var favoriteFragment: FavoriteFragment
    lateinit var historyFragment: HistoryFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRootBinding.inflate(inflater, container, false)
        tabLayout = binding.favoritesTabLayout
        viewPager = binding.favoritesViewPager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TabbedFragmentAdapter(childFragmentManager, lifecycle)
        favoriteFragment = FavoriteFragment()
        historyFragment = HistoryFragment()
        adapter.apply {
            addFragment(favoriteFragment, "Favorites")
            addFragment(historyFragment, "History")
        }
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()
    }

}