package com.example.newsapplication.view.main.more.common.profile.fragment.profile

import android.Manifest
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
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapplication.R
import com.example.newsapplication.databinding.FragmentProfileBinding
import com.example.newsapplication.model.profile.ProfileInfoModel
import com.example.newsapplication.model.setting.SettingLayoutModel
import com.example.newsapplication.util.Constants.AVATAR_IMAGE
import com.example.newsapplication.util.Constants.CAMERA_PERMISSION_CODE
import com.example.newsapplication.util.Constants.GALLERY_PICK_CODE
import com.example.newsapplication.util.Constants.IMAGE_CAPTURE_CODE
import com.example.newsapplication.util.SessionManager
import com.example.newsapplication.view.main.more.common.profile.adapter.ProfileAdapter
import com.example.newsapplication.view.main.more.common.profile.fragment.avatarImage.FullImageFragment
import com.example.newsapplication.view.main.more.common.profile.fragment.bottomSheet.CameraImageBottomSheet
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class ProfileFragment : Fragment(), CameraImageBottomSheet.Listener {

    private val viewModel: ProfileViewModel by viewModels()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar
    private lateinit var appBar: AppBarLayout
    private lateinit var avatarImageView: ImageView
    private lateinit var cardImageView: CardView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var avatarFrameLayout: FrameLayout
    private lateinit var recyclerViewET: RecyclerView
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var editTextList: List<ProfileInfoModel>
    private lateinit var sessionManager: SessionManager

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
        setupRv()
        listener()

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

    }

    private fun init() {
        toolbar = binding.materialToolbar
        appBar = binding.appBarLayout
        avatarImageView = binding.avatarImageView
        cardImageView = binding.cardImageView
        floatingActionButton = binding.floatingActionButton
        avatarFrameLayout = binding.avatarFrameLayout
        recyclerViewET = binding.recyclerViewET
        profileAdapter = ProfileAdapter()
        sessionManager = SessionManager(requireContext())
        editTextList = listOf(
            ProfileInfoModel("Birthday", "17.11.1985"),
            ProfileInfoModel("Gender", "Male"),
            ProfileInfoModel("Phone", "+16584454545"),
            ProfileInfoModel("E-mail", "johnmark@gmail.com"),
        )
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

    private fun setupToolbar() {
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            setNavigationIconTint(ContextCompat.getColor(requireContext(), R.color.white))
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

    private fun setupRv() {
        recyclerViewET.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = profileAdapter
        }
        profileAdapter.differ.submitList(editTextList)
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
            .setReorderingAllowed(true)
            .addSharedElement(avatarImageView, "shared_image2")
            .add(R.id.profileFragmentContainerView, fullImageFragment)
            .hide(this)
            .addToBackStack(null)
            .commit()
    }

    override fun onItemClick(item: SettingLayoutModel) {
        when (item.title) {

            "Take a photo" -> {
                checkCameraPermission()
            }

            "Choose from gallery" -> {
                openGallery()
            }

            "Delete" -> {
                sessionManager.deleteAvatarImage()
                deleteImageFile()
                sessionManager.setShowDelete(0)
                Toast.makeText(requireContext(), "Photo deleted", Toast.LENGTH_SHORT).show()
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
                            // Создаем целевой файл
                            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                            vFilename = "FOTO_$timeStamp.jpg"
                            file = File(
                                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                vFilename
                            )

                            // Копируем данные из URI в файл
                            val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri)
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

                            Toast.makeText(requireContext(), "Image saved: $uri", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
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
}