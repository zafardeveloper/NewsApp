package com.example.newsapplication.view.main.categories.bottomSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.newsapplication.R
import com.example.newsapplication.databinding.CategoriesBottomSheetBinding
import com.example.newsapplication.view.main.categories.CategoriesViewModel
import com.fin_group.artelmark.util.BaseBottomSheetDialogFragment
import com.google.android.material.chip.Chip


class CategoriesBottomSheet() : BaseBottomSheetDialogFragment() {

    private var _binding: CategoriesBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoriesViewModel by viewModels()
    private var listener: Listener? = null

    private val chipToViewpagerMap: Map<Int, Int> = mapOf(
        R.id.chipLocal to 0,
        R.id.chipPolitics to 1,
        R.id.chipEconomy to 2,
        R.id.chipTechnologies to 3,
        R.id.chipSport to 4,
        R.id.chipCulture to 5,
        R.id.chipHealth to 6,
        R.id.chipTravel to 7,
        R.id.chipScience to 8,
        R.id.chipCars to 9,
        R.id.chipSociety to 10,
        R.id.chipEntertainment to 11,
        R.id.chipIncidents to 12,
        R.id.chipFashion to 13,
        R.id.chipWeather to 14,
        R.id.chipEducation to 15,
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when {
            parentFragment is Listener -> parentFragment as Listener
            context is Listener -> context
            else -> throw RuntimeException("$context должен реализовать Listener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CategoriesBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chipGroup = binding.chipGroup
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.setOnClickListener {
                val viewpagerPosition = chipToViewpagerMap[chip.id]
                viewpagerPosition?.let { position ->
                    listener?.onChipClicked(position)
                }
                dismiss()
            }
        }
    }

    interface Listener {
        fun onChipClicked(viewpagerPosition: Int)
    }


}