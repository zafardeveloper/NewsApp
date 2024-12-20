package com.example.quicknews.view.main.more.common.profile.fragment.avatarImage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.quicknews.R
import com.example.quicknews.databinding.FragmentFullImageBinding
import com.example.quicknews.util.Constants.AVATAR_IMAGE
import com.example.quicknews.util.Util.Companion.hideToolbar
import com.example.quicknews.util.Util.Companion.showToolbar
import com.google.android.material.appbar.MaterialToolbar

class FullImageFragment : Fragment() {

    private var _binding: FragmentFullImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var fullAvatarImageView: ImageView
    private lateinit var toolbar: MaterialToolbar
    private var toolbarVisibility = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullImageBinding.inflate(inflater, container, false)
        init()
        setupToolbar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val avatarImage = arguments?.getString(AVATAR_IMAGE)
        avatarImage?.let {
            Glide.with(requireContext()).load(it).dontAnimate().into(fullAvatarImageView)
        }
        fullAvatarImageView.setOnClickListener {
            toolbarVisibility = ! toolbarVisibility
            if (toolbarVisibility) {
                showToolbar(toolbar)
            } else {
                hideToolbar(toolbar)
            }
        }
    }

    private fun init() {
        fullAvatarImageView = binding.fullAvatarImageView
        toolbar = binding.materialToolbar
    }

    private fun setupToolbar() {
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
            setNavigationIconTint(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }


}