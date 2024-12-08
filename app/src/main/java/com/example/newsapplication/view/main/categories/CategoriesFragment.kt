package com.example.newsapplication.view.main.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.newsapplication.databinding.FragmentCategoriesBinding
import com.example.newsapplication.view.main.categories.bottomSheet.CategoriesBottomSheet
import com.example.newsapplication.view.main.categories.tabFragment.TabFragment
import com.example.newsapplication.view.main.categories.tabbedFragmentAdapter.PagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CategoriesFragment : Fragment(), CategoriesBottomSheet.Listener {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoriesViewModel by viewModels()

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: PagerAdapter
    private lateinit var listBtn: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        tabLayout = binding.categoriesTabLayout
        viewPager = binding.categoriesViewPager
        listBtn = binding.list
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PagerAdapter(childFragmentManager, lifecycle)
        adapter.apply {
            addFragment(TabFragment.newInstance("Politics"), "Politics")
            addFragment(TabFragment.newInstance("Economy"), "Economy")
            addFragment(TabFragment.newInstance("Technologies"), "Technologies")
            addFragment(TabFragment.newInstance("Sport"), "Sport")
            addFragment(TabFragment.newInstance("Culture"), "Culture")
            addFragment(TabFragment.newInstance("Health"), "Health")
            addFragment(TabFragment.newInstance("Travel"), "Travel")
            addFragment(TabFragment.newInstance("Science"), "Science")
            addFragment(TabFragment.newInstance("Cars"), "Cars")
            addFragment(TabFragment.newInstance("Society"), "Society")
            addFragment(TabFragment.newInstance("Entertainment"), "Entertainment")
            addFragment(TabFragment.newInstance("Incidents"), "Incidents")
            addFragment(TabFragment.newInstance("Fashion"), "Fashion")
            addFragment(TabFragment.newInstance("Weather"), "Weather")
            addFragment(TabFragment.newInstance("Education"), "Education")
        }
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        listBtn.setOnClickListener {
            val existingDialog =
                childFragmentManager.findFragmentByTag("CategoriesBottomSheetFragment")
            if (existingDialog == null) {
                val fragment = CategoriesBottomSheet()
                fragment.show(childFragmentManager, "CategoriesBottomSheetFragment")
            }
        }

    }

    override fun onChipClicked(viewpagerPosition: Int) {
        viewPager.setCurrentItem(viewpagerPosition, true)
    }
}