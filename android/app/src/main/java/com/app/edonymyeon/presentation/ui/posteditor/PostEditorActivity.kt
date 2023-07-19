package com.app.edonymyeon.presentation.ui.posteditor

import android.Manifest
import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostEditorBinding
import com.app.edonymyeon.presentation.ui.posteditor.adapter.PostEditorImagesAdapter
import com.google.android.material.snackbar.Snackbar

class PostEditorActivity : AppCompatActivity() {

    private val viewModel: PostEditorViewModel by viewModels()
    private val adapter: PostEditorImagesAdapter by lazy {
        PostEditorImagesAdapter(::deleteImages)
    }
    private val binding: ActivityPostEditorBinding by lazy {
        ActivityPostEditorBinding.inflate(layoutInflater)
    }
    private val pickMultipleImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                setGalleryImages(it.data)
            }
        }

    private val takeCameraImage =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                setCameraImage(bitmap)
            }
        }

    private val permissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions: Map<String, Boolean> ->
        permissions.entries.forEach { entry ->
            val isGranted = entry.value
            if (!isGranted) {
                showPermissionSnackbar()
            } else {
                navigateToCamera()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initBinding()
        initAppbar()
        setCameraAndGalleryClickListener()
        observeImages()
        setAdapter()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initAppbar() {
        setSupportActionBar(binding.tbPostEditor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    private fun setCameraAndGalleryClickListener() {
        binding.ivPostCamera.setOnClickListener { requestPermission() }
        binding.ivPostGallery.setOnClickListener { navigateToGallery() }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val backgroundPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            val permissionList = requestPermissions.toMutableList()
            if (permissionList.contains(backgroundPermission)) {
                permissionList.remove(backgroundPermission)
            }
            val permissionArray = permissionList.toTypedArray()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                navigateToCamera()
                return
            }
            permissionRequestLauncher.launch(permissionArray)
        } else {
            permissionRequestLauncher.launch(requestPermissions)
        }
    }

    private fun setAdapter() {
        binding.rvPostEditorImages.adapter = adapter
    }

    private fun deleteImages(image: String) {
        viewModel.deleteSelectedImages(image)
    }

    private fun observeImages() {
        viewModel.galleryImages.observe(
            this,
            Observer { images ->
                adapter.submitList(images)
            },
        )
    }

    private fun showPermissionSnackbar() {
        Snackbar.make(
            binding.clPostEditor,
            getString(R.string.post_editor_check_permission),
            Snackbar.LENGTH_LONG,
        )
            .setAction(getString(R.string.post_editor_snackbar_ok_message)) { openAppSetting() }
            .show()
    }

    private fun openAppSetting() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun navigateToGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pickMultipleImage.launch(intent)
    }

    private fun navigateToCamera() {
        takeCameraImage.launch(null)
    }

    private fun setGalleryImages(data: Intent?) {
        data?.clipData?.let { clipData ->
            val count = clipData.itemCount
            val currentImageCount = viewModel.galleryImages.value?.size ?: 0
            when {
                checkImageCountLimit(count, MAX_IMAGES_COUNT).not() -> return
                checkImageCountLimit(count, MAX_IMAGES_COUNT - currentImageCount).not() -> return
                else -> addSelectedImagesFromClipData(count, clipData)
            }
        } ?: addSelectedImageFromUri(data)
        setAdapter()
    }

    private fun addSelectedImageFromUri(data: Intent?) {
        data?.data?.let { imageUri ->
            viewModel.addSelectedImages(imageUri.toString())
        }
    }

    private fun addSelectedImagesFromClipData(count: Int, clipData: ClipData) {
        for (i in 0 until count) {
            val imageUri = clipData.getItemAt(i).uri
            viewModel.addSelectedImages(imageUri.toString())
        }
    }

    private fun checkImageCountLimit(count: Int, limitCount: Int): Boolean {
        if (count > limitCount) {
            Snackbar.make(
                binding.clPostEditor,
                getString(R.string.post_editor_image_limit_message),
                Snackbar.LENGTH_SHORT,
            ).show()
            return false
        }
        return true
    }

    private fun setCameraImage(bitmap: Bitmap) {
        val imageUri = getImageUri(bitmap)
        viewModel.addSelectedImages(imageUri.toString())
    }

    private fun getImageUri(bitmap: Bitmap): Uri? {
        val resolver = applicationContext.contentResolver
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?.let { imageUri ->
                val outputStream = resolver.openOutputStream(imageUri)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
                return imageUri
            }
        return null
    }

    companion object {
        private const val MAX_IMAGES_COUNT = 10
        private val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "ImageTitle")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        private val requestPermissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }
}