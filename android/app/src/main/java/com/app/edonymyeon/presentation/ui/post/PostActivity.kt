package com.app.edonymyeon.presentation.ui.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.presentation.ui.post.adapter.PostAdapter
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity
import com.app.edonymyeon.presentation.ui.posteditor.PostEditorActivity

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
        viewModel.posts.observe(this) { it ->
            binding.rvPost.adapter = PostAdapter(it, onClick = { postId ->
                startActivity(PostDetailActivity.newIntent(this, postId))
            })
        }
        viewModel.getPosts(20, 0)

        binding.ivPostNew.setOnClickListener {
            startPostEditorActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPosts(20, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> {
                false
            }
        }
    }

    private fun startPostEditorActivity() {
        startActivity(PostEditorActivity.newIntent(this, PostEditorActivity.POST_CODE))
        viewModel.getPosts(20, 0)
    }

    private fun initAppbar() {
        setSupportActionBar(binding.tbPost)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.post_all_post)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PostActivity::class.java)
        }
    }
}
