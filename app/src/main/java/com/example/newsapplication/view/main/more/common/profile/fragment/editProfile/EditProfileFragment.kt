package com.example.newsapplication.view.main.more.common.profile.fragment.editProfile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentEditProfileBinding
import com.example.newsapplication.model.profile.UserInfoModel
import com.example.newsapplication.util.SessionManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileViewModel by viewModels()
    private lateinit var toolbar: MaterialToolbar
    private lateinit var appBar: AppBarLayout
    private lateinit var nameET: EditText
    private lateinit var usernameET: EditText
    private lateinit var phoneET: EditText
    private lateinit var genderET: EditText
    private lateinit var birthdayET: EditText
    private lateinit var emailET: EditText
    private lateinit var user: UserInfoModel
    private lateinit var editTextList: List<EditText>
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        init()
        user = sessionManager.loadUserInfo()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        fillFields(user)
        checkFields()
        birthdayET.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun init() {
        toolbar = binding.materialToolbar
        appBar = binding.appBarLayout
        nameET = binding.nameET
        usernameET = binding.usernameET
        phoneET = binding.phoneET
        genderET = binding.genderET
        birthdayET = binding.birthdayET
        emailET = binding.emailET
        sessionManager = SessionManager(requireContext())
        editTextList = listOf(
            nameET,
            usernameET,
            phoneET,
            genderET,
            birthdayET,
            emailET
        )
    }

    private fun fillFields(userInfo: UserInfoModel) {
        nameET.setText(userInfo.name)
        usernameET.setText(userInfo.username)
        phoneET.setText(userInfo.phone)
        genderET.setText(userInfo.gender)
        birthdayET.setText(userInfo.birthday)
        emailET.setText(userInfo.email)

        nameET.requestFocus()
    }

    private fun checkFields() {
        editTextList.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (
                        nameET.text.toString().trim() == user.name &&
                        usernameET.text.toString().trim() == user.username &&
                        phoneET.text.toString().trim() == user.phone &&
                        genderET.text.toString().trim() == user.gender &&
                        birthdayET.text.toString().trim() == user.birthday &&
                        emailET.text.toString().trim() == user.email
                    ) {
                        toolbar.menu.findItem(R.id.action).setVisible(false)
                    } else {
                        toolbar.menu.findItem(R.id.action).setVisible(true)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}

            })
        }
    }

    private fun setupToolbar() {
        appBar.setExpanded(false)
        toolbar.apply {
            inflateMenu(R.menu.action_menu)
            menu[0].setIcon(R.drawable.ic_check)
            menu.findItem(R.id.action).setVisible(false)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action -> {
                        val userInfoModel = UserInfoModel(
                            name = nameET.text.toString().trim(),
                            username = usernameET.text.toString().trim(),
                            phone = phoneET.text.toString().trim(),
                            gender = genderET.text.toString().trim(),
                            birthday = birthdayET.text.toString().trim(),
                            email = emailET.text.toString().trim()
                        )
                        sessionManager.saveUserInfo(userInfoModel)
                        parentFragmentManager.popBackStack()
                        true
                    }

                    else -> false
                }
            }
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
            setNavigationIconTint(ContextCompat.getColor(requireContext(), R.color.back_button_color))
        }

    }

    private fun showDatePickerDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.date_picker_layout, requireView() as ViewGroup, false)
        val btnOk = dialogView.findViewById<TextView>(R.id.btnOk)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)

        val currentText = birthdayET.text.toString()
        val calendar = Calendar.getInstance()

        if (currentText.isNotEmpty()) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = dateFormat.parse(currentText)
            date?.let {
                calendar.time = it
            }
        }

        datePicker.updateDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val alertDialog = AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setView(dialogView)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnOk.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month
            val year = datePicker.year

            calendar.set(year, month, day)
            val selectedDate =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            birthdayET.setText(selectedDate)
            alertDialog.dismiss()
        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
}