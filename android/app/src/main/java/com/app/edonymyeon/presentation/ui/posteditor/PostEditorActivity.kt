package com.app.edonymyeon.presentation.ui.posteditor

import android.Manifest
import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.databinding.ActivityPostEditorBinding
import com.app.edonymyeon.presentation.ui.posteditor.adapter.PostEditorImagesAdapter

class PostEditorActivity : AppCompatActivity() {

    private val viewModel: PostEditorViewModel by viewModels()
    private lateinit var adapter: PostEditorImagesAdapter
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

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            results.forEach {
                if (!it.value) {
                    Toast.makeText(applicationContext, "권한 허용 필요", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initBinding()
        initAppbar()
        setCameraAndGalleryClickListener()
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
        binding.ivPostCamera.setOnClickListener { requestPermissionsLauncher.launch(permissionList) }
        binding.ivPostGallery.setOnClickListener { navigateToGallery() }
    }

    private fun setAdapter() {
        adapter = PostEditorImagesAdapter(::deleteImages)
        binding.rvPostEditorImages.adapter = adapter
        updateImages()
    }

    private fun deleteImages(image: String) {
        viewModel.deleteSelectedImages(image)
        setAdapter()
    }

    private fun updateImages() {
        adapter.submitList(viewModel.galleryImages.value)
    }

    private fun navigateToGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pickMultipleImage.launch(intent)
    }

    private fun navigateToCamera() {
        requestPermissionsLauncher.launch(permissionList)
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
            Toast.makeText(applicationContext, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun setCameraImage(bitmap: Bitmap) {
        val imageUri = getImageUri(bitmap)
        viewModel.addSelectedImages(imageUri.toString())
        setAdapter()
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
        private val permissionList = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }
}
