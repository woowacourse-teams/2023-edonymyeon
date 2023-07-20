package com.app.edonymyeon.presentation.ui.post

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostBinding
import com.app.edonymyeon.presentation.ui.post.adapter.PostAdapter

class PostActivity : AppCompatActivity() {
    private val binding: ActivityPostBinding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initAppbar()
        binding.rvPost.adapter = PostAdapter(listOf(), onClick = {
        })

    }

    private fun initAppbar() {
        setSupportActionBar(binding.tbPost)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.post_all_post)
    }
}
