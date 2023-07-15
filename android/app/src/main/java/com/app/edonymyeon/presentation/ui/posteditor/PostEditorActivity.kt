package com.app.edonymyeon.presentation.ui.posteditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.databinding.ActivityPostEditorBinding
import com.app.edonymyeon.presentation.ui.posteditor.adapter.PostEditorImagesAdapter

class PostEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostEditorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAdapter()
    }

    private fun setAdapter() {
        val adapter = PostEditorImagesAdapter(::deleteImage)
        binding.rvPostEditorImages.adapter = adapter
    }

    private fun deleteImage(imageUrl: String) {
    }
}
