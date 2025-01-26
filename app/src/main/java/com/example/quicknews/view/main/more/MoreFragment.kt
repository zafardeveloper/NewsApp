package com.example.quicknews.view.main.more

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknews.R
import com.example.quicknews.common.BaseFragment
import com.example.quicknews.databinding.FragmentMoreBinding
import com.example.quicknews.model.setting.SettingLayoutModel
import com.example.quicknews.util.Constants.DARK_MODE
import com.example.quicknews.util.Constants.LIGHT_MODE
import com.example.quicknews.util.Constants.SEARCH_QUERY
import com.example.quicknews.util.Constants.SYSTEM_MODE
import com.example.quicknews.util.SharedPreferencesUtils.setLanguageCode
import com.example.quicknews.util.SharedPreferencesUtils.setLanguagePosition
import com.example.quicknews.util.ThemeHelper
import com.example.quicknews.view.main.home.HomeViewModel
import com.example.quicknews.view.main.more.adapter.MoreAdapter
import com.example.quicknews.view.main.more.common.history.HistoryActivity
import com.example.quicknews.view.main.more.common.language.CountryBottomSheet
import com.example.quicknews.view.main.more.common.profile.ProfileActivity
import com.example.quicknews.view.main.more.common.readLater.ReadLaterActivity
import com.example.quicknews.view.main.more.common.theme.ThemeBottomSheet

class MoreFragment : BaseFragment(), MoreAdapter.Listener, CountryBottomSheet.Listener, ThemeBottomSheet.Listener {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MoreViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var moreAdapter: MoreAdapter
    private lateinit var appInfoTV: TextView
    private lateinit var settingList: List<SettingLayoutModel>
    private lateinit var searchHistoryLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moreAdapter.differ.submitList(settingList)
        setupRv()
        val versionName = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0).versionName
        appInfoTV.text = getString(R.string.appInfo) + versionName
    }

    private fun init() {
        recyclerView = binding.rvMore
        moreAdapter = MoreAdapter(this)
        appInfoTV = binding.appInfoTV
        settingList = listOf(
            SettingLayoutModel(R.drawable.ic_user, getString(R.string.my_profile)),
            SettingLayoutModel(R.drawable.ic_later, getString(R.string.read_it_later)),
            SettingLayoutModel(R.drawable.ic_history, getString(R.string.history)),
            SettingLayoutModel(R.drawable.ic_language, getString(R.string.country_language)),
            SettingLayoutModel(R.drawable.ic_theme, getString(R.string.theme))
        )
        searchHistoryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.getStringExtra(SEARCH_QUERY)
                    data?.let {
                        homeViewModel.setSearchQuery(data)
                    }
                }
            }
    }

    private fun setupRv() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moreAdapter
        }
    }

    override fun onClick(item: SettingLayoutModel) {
        when (item.title) {
            getString(R.string.my_profile) -> {
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                startActivity(intent)
            }

            getString(R.string.read_it_later) -> {
                val intent = Intent(requireContext(), ReadLaterActivity::class.java)
                startActivity(intent)
            }

            getString(R.string.history) -> {
                val intent = Intent(requireContext(), HistoryActivity::class.java)
                searchHistoryLauncher.launch(intent)
            }

            getString(R.string.country_language) -> {
                showCountryBottomSheet()
            }

            getString(R.string.theme) -> {
                showThemeBottomSheet()
            }

            else -> {
                Toast.makeText(requireContext(), "Click", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCountryBottomSheet() {
        val existingDialog =
            childFragmentManager.findFragmentByTag("CountryBottomSheet")
        if (existingDialog == null) {
            val fragment = CountryBottomSheet()
            fragment.show(childFragmentManager, "CountryBottomSheet")
        }
    }

    private fun showThemeBottomSheet() {
        val existingDialog =
            childFragmentManager.findFragmentByTag("ThemeBottomSheet")
        if (existingDialog == null) {
            val fragment = ThemeBottomSheet()
            fragment.show(childFragmentManager, "ThemeBottomSheet")
        }
    }

    override fun onCountryClick(code: String) {
        when (code) {

            "en" -> {
                setLanguageCode(requireContext(), "en")
                setLanguagePosition(requireContext(), 0)
            }

            "ru" -> {
                setLanguageCode(requireContext(), "ru")
                setLanguagePosition(requireContext(), 1)
            }
        }
        requireActivity().recreate()
    }

    override fun onThemeClick(theme: String) {
        when (theme) {
            "light" -> {
                ThemeHelper.setThemeMode(requireContext(), LIGHT_MODE)
            }

            "dark" -> {
                ThemeHelper.setThemeMode(requireContext(), DARK_MODE)
            }

            "system" -> {
                ThemeHelper.setThemeMode(requireContext(), SYSTEM_MODE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}