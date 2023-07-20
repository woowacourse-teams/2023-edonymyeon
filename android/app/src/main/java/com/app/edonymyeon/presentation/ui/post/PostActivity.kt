package com.app.edonymyeon.presentation.ui.post

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.presentation.ui.post.adapter.PostAdapter

class PostActivity : AppCompatActivity() {
    private val binding: ActivityPostBinding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }

    private val viewModel: PostViewModel by lazy {
        PostViewModelFactory(PostRepositoryImpl(PostRemoteDataSource())).create(PostViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initAppbar()
        viewModel.posts.observe(this) {
            binding.rvPost.adapter = PostAdapter(it, onClick = {
            })
        }
        viewModel.getPosts(20,0)
    }

    private fun initAppbar() {
        setSupportActionBar(binding.tbPost)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.post_all_post)
    }
}
