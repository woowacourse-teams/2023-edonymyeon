package com.app.edonymyeon.presentation.ui.postdetail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostDetailBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.google.android.material.snackbar.Snackbar

class PostDetailActivity : AppCompatActivity() {
    private val binding: ActivityPostDetailBinding by lazy {
        ActivityPostDetailBinding.inflate(layoutInflater)
    }

    private val viewModel: PostDetailViewModel by viewModels {
        PostDetailViewModelFactory(
            0L,
            PostRepositoryImpl(PostRemoteDataSource()),
        )
    }

    private val dialog: DeleteDialog by lazy {
        DeleteDialog {
            viewModel.deletePost()
            // 게시글 목록으로 이동
            dialog.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initBinding()
        initAppbar()
        val id = 0L
//        viewModel.getPostDetail(id)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post_detail, menu)
        hideMenusForWriter(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_update -> {
                Snackbar.make(binding.root, "update", Snackbar.LENGTH_SHORT).show()
                true
            }

            R.id.action_delete -> {
                dialog.show(supportFragmentManager, "DeleteDialog")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun hideMenusForWriter(menu: Menu?) {
        listOf(
            menu?.findItem(R.id.action_update),
            menu?.findItem(R.id.action_delete),
        ).forEach {
            if (isMyPost()) {
                it?.isVisible = false
            }
        }
    }

    private fun isMyPost() = viewModel.post.value?.isWriter == true

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.postViewModel = viewModel
    }

    private fun initAppbar() {
        setSupportActionBar(binding.tbPostDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        binding.actionScrap.setOnCheckedChangeListener { _, isChecked ->
            if (isMyPost()) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.post_detail_writer_cant_scrap),
                    Snackbar.LENGTH_SHORT,
                ).show()
                binding.actionScrap.isChecked = false
                return@setOnCheckedChangeListener
            }
            viewModel.updateScrap(isChecked)
        }
    }
}
