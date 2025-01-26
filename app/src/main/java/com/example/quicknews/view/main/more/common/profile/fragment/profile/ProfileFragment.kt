package com.example.quicknews.view.main.more.common.profile.fragment.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.quicknews.R
import com.example.quicknews.common.BaseFragment
import com.example.quicknews.databinding.FragmentProfileBinding
import com.example.quicknews.model.profile.UserInfoModel
import com.example.quicknews.model.setting.SettingLayoutModel
import com.example.quicknews.util.Constants.AVATAR_IMAGE
import com.example.quicknews.util.Constants.CAMERA_PERMISSION_CODE
import com.example.quicknews.util.Constants.GALLERY_PICK_CODE
import com.example.quicknews.util.Constants.IMAGE_CAPTURE_CODE
import com.example.quicknews.util.SessionManager
import com.example.quicknews.view.main.more.common.profile.fragment.avatarImage.FullImageFragment
import com.example.quicknews.view.main.more.common.profile.fragment.bottomSheet.CameraImageBottomSheet
import com.example.quicknews.view.main.more.common.profile.fragment.editProfile.EditProfileFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class ProfileFragment : BaseFragment(), CameraImageBottomSheet.Listener {

    private val viewModel: ProfileViewModel by activityViewModels()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBar: AppBarLayout
    private lateinit var avatarImageView: ImageView
    private lateinit var cardImageView: CardView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var avatarFrameLayout: FrameLayout
    private lateinit var sessionManager: SessionManager
    private lateinit var usernameET: TextView
    private lateinit var phoneET: TextView
    private lateinit var genderET: TextView
    private lateinit var birthdayET: TextView
    private lateinit var emailET: TextView
    private lateinit var user: UserInfoModel

    private var showDelete: Int = 0
    private var vFilename: String = ""
    private lateinit var file: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        init()
        file = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            vFilename
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        listener()
        observeLiveData()
        fillFields(user)
    }

    private fun init() {
        toolbar = binding.materialToolbar
        collapsingToolbarLayout = binding.collapsingToolbarLayout
        appBar = binding.appBarLayout
        avatarImageView = binding.avatarImageView
        cardImageView = binding.cardImageView
        floatingActionButton = binding.floatingActionButton
        avatarFrameLayout = binding.avatarFrameLayout
        usernameET = binding.usernameTV
        phoneET = binding.phoneTV
        genderET = binding.genderTV
        birthdayET = binding.birthdayTV
        emailET = binding.emailTV
        sessionManager = SessionManager(requireContext())
        user = sessionManager.loadUserInfo()
    }

    private fun listener() {

        avatarImageView.setOnClickListener {
            if (showDelete == 0) {
                showBottomSheet()
            } else {
                showFullImageFragment()
            }
        }

        floatingActionButton.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun observeLiveData() {
        sessionManager.getShowDeleteLiveData().observe(viewLifecycleOwner) {
            it?.let {
                showDelete = it
            }
        }
        sessionManager.getAvatarImageLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                Glide.with(requireContext()).load(it).into(avatarImageView)
            } else {
                avatarImageView.setImageResource(R.drawable.ic_avatar)
            }
        }
        sessionManager.refreshUserInfoLiveData().observe(viewLifecycleOwner) {
            fillFields(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fillFields(userInfo: UserInfoModel) {
        if (userInfo.username.isNotEmpty()) {
            usernameET.text = "@" + userInfo.username
        }
        phoneET.text = userInfo.phone
        genderET.text = userInfo.gender
        birthdayET.text = userInfo.birthday
        emailET.text = userInfo.email
        collapsingToolbarLayout.title = userInfo.name
    }


    private fun setupToolbar() {
        toolbar.apply {
            inflateMenu(R.menu.action_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action -> {
                        val editProfileFragment = EditProfileFragment()
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.overlay_from_right,
                                R.anim.overlay_to_left,
                                R.anim.overlay_from_left,
                                R.anim.overlay_to_right
                            )
                            .replace(R.id.profileFragmentContainerView, editProfileFragment)
                            .addToBackStack(null)
                            .commit()
                        true
                    }

                    else -> false
                }
            }
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            setNavigationIconTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.back_button_color
                )
            )
        }
        appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val collapseRange = appBarLayout.totalScrollRange
            val collapsePercentage = abs(verticalOffset).toFloat() / collapseRange
            avatarFrameLayout.apply {
                alpha = 1 - collapsePercentage
                scaleX = 1 - collapsePercentage * 0.5f
                scaleY = 1 - collapsePercentage * 0.5f
            }
        }
    }

    private fun showBottomSheet() {
        val existingDialog =
            childFragmentManager.findFragmentByTag("CameraImageBottomSheet")
        if (existingDialog == null) {
            val bundle = Bundle().apply {
                putInt("Avatar", showDelete)
            }
            val fragment = CameraImageBottomSheet()
            fragment.arguments = bundle
            fragment.show(childFragmentManager, "CameraImageBottomSheet")
        }
    }

    private fun showFullImageFragment() {
        val fullImageFragment = FullImageFragment()
        val currentAvatarUri = sessionManager.getAvatarImageLiveData().value
        val bundle = Bundle().apply {
            putString(AVATAR_IMAGE, currentAvatarUri)
        }
        fullImageFragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .add(R.id.profileFragmentContainerView, fullImageFragment)
            .hide(this)
            .addToBackStack(null)
            .commit()
    }

    override fun onItemClick(item: SettingLayoutModel) {
        when (item.title) {

            getString(R.string.take_a_photo) -> {
                checkCameraPermission()
            }

            getString(R.string.choose_from_gallery) -> {
                openGallery()
            }

            getString(R.string.delete) -> {
                sessionManager.deleteAvatarImage()
                deleteImageFile()
                sessionManager.setShowDelete(0)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.photo_deleted), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    @Suppress("DEPRECATION")
    private fun openCamera() {
        if (::file.isInitialized && file.exists()) {
            val isDeleted = file.delete()
            if (isDeleted) {
                Log.d("MyLog", "Старое фото успешно удалено")
            } else {
                Log.d("MyLog", "Ошибка удаления старого фото")
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        vFilename = "FOTO_$timeStamp.jpg"

        val file =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), vFilename)

        val imageUri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName + ".provider",
            file
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    @Suppress("DEPRECATION")
    private fun openGallery() {
        if (::file.isInitialized && file.exists()) {
            val isDeleted = file.delete()
            if (isDeleted) {
                Log.d("MyLog", "Старое фото успешно удалено")
            } else {
                Log.d("MyLog", "Ошибка удаления старого фото")
            }
        }
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_PICK_CODE)
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_CAPTURE_CODE -> {
                if (resultCode == RESULT_OK) {
                    file = File(
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        vFilename
                    )
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().applicationContext.packageName + ".provider", file
                    )
                    sessionManager.setAvatarImage(uri)
                    sessionManager.setShowDelete(1)
                    Toast.makeText(requireContext(), "Image saved: $uri", Toast.LENGTH_SHORT).show()
                }
            }

            GALLERY_PICK_CODE -> {
                if (resultCode == RESULT_OK && data != null) {
                    val selectedImageUri = data.data
                    if (selectedImageUri != null) {
                        try {
                            val timeStamp = SimpleDateFormat(
                                "yyyyMMdd_HHmmss",
                                Locale.getDefault()
                            ).format(Date())
                            vFilename = "FOTO_$timeStamp.jpg"
                            file = File(
                                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                vFilename
                            )
                            val inputStream =
                                requireContext().contentResolver.openInputStream(selectedImageUri)
                            val outputStream = file.outputStream()

                            inputStream?.use { input ->
                                outputStream.use { output ->
                                    input.copyTo(output)
                                }
                            }

                            val uri = FileProvider.getUriForFile(
                                requireContext(),
                                requireContext().applicationContext.packageName + ".provider",
                                file
                            )
                            sessionManager.setAvatarImage(uri)
                            sessionManager.setShowDelete(1)

                            Toast.makeText(
                                requireContext(),
                                "Image saved: $uri",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                requireContext(),
                                "Failed to save image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun deleteImageFile() {
        if (file.exists()) {
            val isDeleted = file.delete()
            if (isDeleted) {
                Log.d("MyLog", "Successfully deleted")
            } else {
                Log.d("MyLog", "Error deleting")
            }
        } else {
            Log.d("MyLog", "File not found: ${file.absolutePath}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}