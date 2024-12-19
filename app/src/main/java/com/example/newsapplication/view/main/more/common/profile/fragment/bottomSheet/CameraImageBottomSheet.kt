package com.example.newsapplication.view.main.more.common.profile.fragment.bottomSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.databinding.BottomSheetListBinding
import com.example.newsapplication.model.setting.SettingLayoutModel
import com.example.newsapplication.view.main.more.common.profile.fragment.bottomSheet.adapter.CameraImageAdapter
import com.fin_group.artelmark.util.BaseBottomSheetDialogFragment

class CameraImageBottomSheet : BaseBottomSheetDialogFragment(), CameraImageAdapter.Listener {

    private var _binding: BottomSheetListBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var cameraImageAdapter: CameraImageAdapter
    private lateinit var bottomSheetItems: List<SettingLayoutModel>
    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when {
            parentFragment is Listener -> parentFragment as Listener
            context is Listener -> context
            else -> throw RuntimeException("$context должен реализовать Listener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetListBinding.inflate(inflater, container, false)
        init()
        setupRv()
        return binding.root
    }

    private fun init() {
        recyclerView = binding.recyclerView
        cameraImageAdapter = CameraImageAdapter(this)
        val showDelete = arguments?.getInt("Avatar")
        bottomSheetItems = if (showDelete != 0) {
            listOf(
                SettingLayoutModel(R.drawable.ic_camera, getString(R.string.take_a_photo)),
                SettingLayoutModel(R.drawable.ic_image, getString(R.string.choose_from_gallery)),
                SettingLayoutModel(R.drawable.ic_trash, getString(R.string.delete))
            )
        } else {
            listOf(
                SettingLayoutModel(R.drawable.ic_camera, getString(R.string.take_a_photo)),
                SettingLayoutModel(R.drawable.ic_image, getString(R.string.choose_from_gallery))
            )
        }
    }

    private fun setupRv() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cameraImageAdapter
        }
        cameraImageAdapter.differ.submitList(bottomSheetItems)
    }

    override fun onClick(item: SettingLayoutModel) {
        listener?.onItemClick(item)
        dismiss()
    }

    interface Listener {
        fun onItemClick(item: SettingLayoutModel)
    }


}