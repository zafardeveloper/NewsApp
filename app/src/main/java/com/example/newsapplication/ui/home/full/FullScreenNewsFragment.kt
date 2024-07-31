package com.example.newsapplication.ui.home.full

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentFullScreenNewsBinding
import com.example.newsapplication.util.formatDate
import com.google.android.material.appbar.MaterialToolbar
import com.squareup.picasso.Picasso

class FullScreenNewsFragment : Fragment() {

    private var _binding: FragmentFullScreenNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FullScreenNewsViewModel by viewModels()
    private val args: FullScreenNewsFragmentArgs by navArgs()
    private lateinit var toolbar: MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullScreenNewsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = requireActivity().findViewById(R.id.materialToolbar)
        toolbar.title = args.newsData.source?.name
        if (args.newsData.urlToImage == null) {
            binding.ivArticleImage.setImageResource(R.drawable.ic_no_image)
        } else {
            Picasso.get().load(args.newsData.urlToImage).into(binding.ivArticleImage)
        }
        binding.tvTitle.text = args.newsData.title
        binding.tvDescription.text = args.newsData.description
        binding.tvPublishedAt.text =
            formatDate(args.newsData.publishedAt!!, "yyyy-MM-dd'T'HH:mm:ss'Z'", "dd MMMM HH:mm")
    }
}