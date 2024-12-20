package com.example.quicknews.view.main.more.common.theme

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.quicknews.R
import com.example.quicknews.databinding.BottomSheetThemeBinding
import com.example.quicknews.util.Constants.DARK_MODE
import com.example.quicknews.util.Constants.LIGHT_MODE
import com.example.quicknews.util.Constants.SYSTEM_MODE
import com.example.quicknews.util.SharedPreferencesUtils
import com.fin_group.artelmark.util.BaseBottomSheetDialogFragment

class ThemeBottomSheet : BaseBottomSheetDialogFragment() {

    private var _binding: BottomSheetThemeBinding? = null
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
        _binding = BottomSheetThemeBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeRadioButtonViews()
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
                        listener?.onThemeClick("light")
                        dismiss()
                    }

                R.id.rb2 ->
                    if (checked) {
                        listener?.onThemeClick("dark")
                        dismiss()
                    }

                R.id.rb3 ->
                    if (checked) {
                        listener?.onThemeClick("system")
                        dismiss()
                    }

            }
        }
    }

    private fun changeRadioButtonViews() {
        when (SharedPreferencesUtils.getThemeMode(requireContext())) {
            LIGHT_MODE -> binding.rb1.isChecked = true
            DARK_MODE -> binding.rb2.isChecked = true
            SYSTEM_MODE -> binding.rb3.isChecked = true
        }
    }

    interface Listener {
        fun onThemeClick(theme: String)
    }
}