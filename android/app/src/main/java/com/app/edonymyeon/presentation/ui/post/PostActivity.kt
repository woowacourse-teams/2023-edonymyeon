package com.app.edonymyeon.presentation.ui.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostBinding
import com.app.edonymyeon.presentation.common.activity.BaseActivity
import com.app.edonymyeon.presentation.ui.login.LoginActivity
import com.app.edonymyeon.presentation.ui.post.adapter.PostAdapter
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity
import com.app.edonymyeon.presentation.ui.posteditor.PostEditorActivity
import com.app.edonymyeon.presentation.util.makeSnackbarWithEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostActivity : BaseActivity<ActivityPostBinding, PostViewModel>(
    { ActivityPostBinding.inflate(it) }
) {
    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == PostEditorActivity.RESULT_RELOAD_CODE) {
                loadNewData()
            }
        }

    override val viewModel: PostViewModel by viewModels()
    override val inflater: LayoutInflater by lazy { LayoutInflater.from(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initAppbar()
        setPostAdapter()
        setListener()
        loadNewData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initAppbar() {
        setSupportActionBar(binding.tbPost)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.post_all_post)
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

    private fun setListener() {
        binding.fabPostNew.setOnClickListener {
            if (viewModel.isLogin) {
                navigateToPostEditor()
            } else {
                binding.root.makeSnackbarWithEvent(
                    message = getString(R.string.all_required_login),
                    eventTitle = getString(R.string.login_title),
                ) { navigateToLogin() }
            }
        }

        binding.srlRefresh.setOnRefreshListener {
            binding.srlRefresh.isRefreshing = false
            loadNewData()
        }
    }

    private fun loadNewData() {
        viewModel.clearResult()
        viewModel.getPosts()
    }

    private fun navigateToPostEditor() {
        activityLauncher.launch(PostEditorActivity.newIntent(this, PostEditorActivity.POST_CODE))
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
