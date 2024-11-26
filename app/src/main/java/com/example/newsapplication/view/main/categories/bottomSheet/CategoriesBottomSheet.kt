package com.example.newsapplication.view.main.categories.bottomSheet

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


class CategoriesBottomSheet(private val listener: Listener) : BaseBottomSheetDialogFragment() {

    private var _binding: CategoriesBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoriesViewModel by viewModels()

    private val chipToViewpagerMap: Map<Int, Int> = mapOf(
        R.id.chipPolitics to 0,
        R.id.chipEconomy to 1,
        R.id.chipTechnologies to 2,
        R.id.chipSport to 3,
        R.id.chipCulture to 4,
        R.id.chipHealth to 5,
        R.id.chipTravel to 6,
        R.id.chipScience to 7,
        R.id.chipCars to 8,
        R.id.chipSociety to 9,
        R.id.chipEntertainment to 10,
        R.id.chipIncidents to 11,
        R.id.chipFashion to 12,
        R.id.chipWeather to 13,
        R.id.chipEducation to 14,
    )

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
                    listener.onChipClicked(position)
                }
                dismiss()
            }
        }
    }

    interface Listener {
        fun onChipClicked(viewpagerPosition: Int)
    }


}