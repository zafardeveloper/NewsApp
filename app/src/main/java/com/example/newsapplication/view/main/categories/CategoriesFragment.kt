package com.example.newsapplication.view.main.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.newsapplication.R
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
            addFragment(TabFragment.newInstance(getString(R.string.local)), getString(R.string.local))
            addFragment(TabFragment.newInstance(getString(R.string.politics)), getString(R.string.politics))
            addFragment(TabFragment.newInstance(getString(R.string.economy)), getString(R.string.economy))
            addFragment(TabFragment.newInstance(getString(R.string.technologies)), getString(R.string.technologies))
            addFragment(TabFragment.newInstance(getString(R.string.sport)), getString(R.string.sport))
            addFragment(TabFragment.newInstance(getString(R.string.culture)), getString(R.string.culture))
            addFragment(TabFragment.newInstance(getString(R.string.health)), getString(R.string.health))
            addFragment(TabFragment.newInstance(getString(R.string.travel)), getString(R.string.travel))
            addFragment(TabFragment.newInstance(getString(R.string.science)), getString(R.string.science))
            addFragment(TabFragment.newInstance(getString(R.string.cars)), getString(R.string.cars))
            addFragment(TabFragment.newInstance(getString(R.string.society)), getString(R.string.society))
            addFragment(TabFragment.newInstance(getString(R.string.entertainment)), getString(R.string.entertainment))
            addFragment(TabFragment.newInstance(getString(R.string.incidents)), getString(R.string.incidents))
            addFragment(TabFragment.newInstance(getString(R.string.fashion)), getString(R.string.fashion))
            addFragment(TabFragment.newInstance(getString(R.string.weather)), getString(R.string.weather))
            addFragment(TabFragment.newInstance(getString(R.string.education)), getString(R.string.education))
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