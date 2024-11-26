package com.example.newsapplication.view.main.more

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.databinding.FragmentMoreBinding
import com.example.newsapplication.view.main.more.adapter.MoreAdapter

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MoreViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var moreAdapter: MoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        recyclerView = binding.rvMore
        moreAdapter = MoreAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRv()
    }

    private fun setupRv() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moreAdapter
        }
    }
}