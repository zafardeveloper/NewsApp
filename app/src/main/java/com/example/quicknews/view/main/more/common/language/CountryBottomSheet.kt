package com.example.quicknews.view.main.more.common.language

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.quicknews.R
import com.example.quicknews.databinding.BottomSheetCountryBinding
import com.example.quicknews.util.SharedPreferencesUtils.getLanguagePosition
import com.fin_group.artelmark.util.BaseBottomSheetDialogFragment

class CountryBottomSheet : BaseBottomSheetDialogFragment() {

    private var _binding: BottomSheetCountryBinding? = null
    private val binding get() = _binding!!

    private lateinit var radioGroup: RadioGroup
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
        _binding = BottomSheetCountryBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeRadioButtonViews(getLanguagePosition(requireContext()))
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = group.findViewById(checkedId)
            onRadioButtonClicked(radio)
        }
    }

    private fun init() {
        radioGroup = binding.radioGroup
    }

    private fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.rb1 ->
                    if (checked) {
                        listener?.onCountryClick("en")
                        dismiss()
                    }

                R.id.rb2 ->
                    if (checked) {
                        listener?.onCountryClick("ru")
                        dismiss()
                    }

            }
        }
    }

    private fun changeRadioButtonViews(position: Int) {
        when (position) {
            0 -> binding.rb1.isChecked = true
            1 -> binding.rb2.isChecked = true
        }
    }

    interface Listener {
        fun onCountryClick(code: String)
    }
}