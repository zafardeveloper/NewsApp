package com.example.newsapplication.view.categories

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.newsapplication.databinding.FragmentCategoriesBinding
import com.example.newsapplication.view.categories.singleFragment.SingleFragment
import com.example.newsapplication.view.categories.tabbedFragmentAdapter.PagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CategoriesFragment : Fragment() {

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
            addFragment(SingleFragment.newInstance("Politics"), "Politics")
            addFragment(SingleFragment.newInstance("Economy"), "Economy")
            addFragment(SingleFragment.newInstance("Technologies"), "Technologies")
            addFragment(SingleFragment.newInstance("Sport"), "Sport")
            addFragment(SingleFragment.newInstance("Culture"), "Culture")
            addFragment(SingleFragment.newInstance("Health"), "Health")
            addFragment(SingleFragment.newInstance("Travel"), "Travel")
            addFragment(SingleFragment.newInstance("Science"), "Science")
            addFragment(SingleFragment.newInstance("Cars"), "Cars")
            addFragment(SingleFragment.newInstance("Society"), "Society")
            addFragment(SingleFragment.newInstance("Entertainment"), "Entertainment")
            addFragment(SingleFragment.newInstance("Incidents"), "Incidents")
            addFragment(SingleFragment.newInstance("Fashion"), "Fashion")
            addFragment(SingleFragment.newInstance("Weather"), "Weather")
            addFragment(SingleFragment.newInstance("Education"), "Education")
        }
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        listBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
        }


    }
}