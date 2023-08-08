package com.app.edonymyeon.presentation.ui.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostBinding
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.data.util.PreferenceUtil
import com.app.edonymyeon.presentation.ui.login.LoginActivity
import com.app.edonymyeon.presentation.ui.post.adapter.PostAdapter
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity
import com.app.edonymyeon.presentation.ui.posteditor.PostEditorActivity
import com.app.edonymyeon.presentation.util.makeSnackbarWithEvent

class PostActivity : AppCompatActivity() {
    private val binding: ActivityPostBinding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }

    private val viewModel: PostViewModel by lazy {
        PostViewModelFactory(PostRepositoryImpl(PostRemoteDataSource())).create(PostViewModel::class.java)
    }

    private val isLogin: Boolean
        get() = PreferenceUtil.getValue(AuthLocalDataSource.USER_ACCESS_TOKEN) != ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initAppbar()
        setPostAdapter()
        setListener()
    }

    private fun setPostAdapter() {
        val postAdapter = PostAdapter(onClick = { postId ->
            startActivity(PostDetailActivity.newIntent(this, postId))
        })
        binding.rvPost.adapter = postAdapter
        setObserver(postAdapter)
        setScrollListener()
    }

    private fun setObserver(postAdapter: PostAdapter) {
        viewModel.posts.observe(this) { it ->
            postAdapter.setPosts(it)
        }
    }

    private fun setScrollListener() {
        binding.rvPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!viewModel.hasNextPage()) {
                    return
                }
                if (!binding.rvPost.canScrollVertically(1)) {
                    viewModel.getPosts()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.clearResult()
        viewModel.getPosts()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

    private fun setListener() {
        binding.ivPostNew.setOnClickListener {
            if (isLogin) {
                navigateToPostEditor()
            } else {
                binding.root.makeSnackbarWithEvent(
                    message = getString(R.string.all_required_login),
                    eventTitle = getString(R.string.login_title),
                ) { navigateToLogin() }
            }
        }
    }

    private fun startPostEditorActivity() {
        startActivity(PostEditorActivity.newIntent(this, PostEditorActivity.POST_CODE))
    }

    private fun navigateToLogin() {
        startActivity(LoginActivity.newIntent(this))
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PostActivity::class.java)
        }
    }
}
