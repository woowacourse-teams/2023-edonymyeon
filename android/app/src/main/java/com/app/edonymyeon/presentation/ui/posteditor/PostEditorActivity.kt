package com.app.edonymyeon.presentation.ui.posteditor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import app.edonymyeon.databinding.ActivityPostEditorBinding
import com.app.edonymyeon.presentation.ui.posteditor.adapter.PostEditorImagesAdapter

class PostEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostEditorBinding
    private val viewModel: PostEditorViewModel by viewModels()
    private lateinit var adapter: PostEditorImagesAdapter
    private val pickMultipleImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                setGalleryImages(it.data)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.ivPostGallery.setOnClickListener {
            navigateToGallery()
        }

        setAdapter()
    }

    private fun setAdapter() {
        adapter = PostEditorImagesAdapter(::deleteImages)
        binding.rvPostEditorImages.adapter = adapter
    }

    private fun deleteImages(image: String) {
        viewModel.deleteSelectedImages(image)
        setAdapter()
        updateImages()
    }

    private fun updateImages() {
        viewModel.galleryImages.observe(
            this,
            Observer {
                adapter.submitList(it)
            },
        )
    }
    private fun navigateToGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pickMultipleImage.launch(intent)
    }

    private fun setGalleryImages(data: Intent?) {
        data?.clipData?.let { clipData ->
            val count = clipData.itemCount
            if (count > MAX_IMAGES_COUNT) {
                Toast.makeText(applicationContext, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
                return
            }
            val currentImageCount = viewModel.galleryImages.value?.size ?: 0
            if (currentImageCount + count > MAX_IMAGES_COUNT) {
                Toast.makeText(applicationContext, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
                return
            }
            for (i in 0 until count) {
                val imageUri = clipData.getItemAt(i).uri
                viewModel.addSelectedImages(imageUri.toString())
            }
        } ?: run {
            data?.data?.let { imageUri ->
                viewModel.addSelectedImages(imageUri.toString())
            }
        }
        updateImages()
    }

    companion object {
        private const val MAX_IMAGES_COUNT = 10
    }
}
