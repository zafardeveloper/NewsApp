package com.example.newsapplication.view.main.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentMoreBinding
import com.example.newsapplication.model.setting.SettingLayoutModel
import com.example.newsapplication.view.main.more.adapter.MoreAdapter
import com.example.newsapplication.view.main.more.common.history.HistoryActivity
import com.example.newsapplication.view.main.more.common.profile.ProfileActivity
import com.example.newsapplication.view.main.more.common.readLater.ReadLaterActivity

class MoreFragment : Fragment(), MoreAdapter.Listener {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MoreViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var moreAdapter: MoreAdapter
    private lateinit var settingList: List<SettingLayoutModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moreAdapter.differ.submitList(settingList)
        setupRv()
    }

    private fun init() {
        recyclerView = binding.rvMore
        moreAdapter = MoreAdapter(this)
        settingList = listOf(
            SettingLayoutModel(R.drawable.ic_user, "My profile"),
            SettingLayoutModel(R.drawable.ic_later, "Read it later"),
            SettingLayoutModel(R.drawable.ic_history, "History"),
            SettingLayoutModel(R.drawable.ic_language, "Country & language"),
            SettingLayoutModel(R.drawable.ic_theme, "Theme"),
            SettingLayoutModel(R.drawable.ic_info, "About app"),
        )
    }

    private fun setupRv() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moreAdapter
        }
    }

    override fun onClick(item: SettingLayoutModel) {
        when (item.title) {
            "My profile" -> {
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                startActivity(intent)
            }

            "Read it later" -> {
                val intent = Intent(requireContext(), ReadLaterActivity::class.java)
                startActivity(intent)
            }

            "History" -> {
                val intent = Intent(requireContext(), HistoryActivity::class.java)
                startActivity(intent)
            }

            else -> {
                Toast.makeText(requireContext(), "Click", Toast.LENGTH_SHORT).show()
            }
        }
    }
}