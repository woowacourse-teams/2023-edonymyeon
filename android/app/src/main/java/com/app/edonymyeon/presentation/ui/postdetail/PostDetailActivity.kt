package com.app.edonymyeon.presentation.ui.postdetail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostDetailBinding
import com.google.android.material.snackbar.Snackbar

class PostDetailActivity : AppCompatActivity() {
    private val binding: ActivityPostDetailBinding by lazy {
        ActivityPostDetailBinding.inflate(layoutInflater)
    }

    private val viewModel: PostDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initAppbar()
        initBinding()

        setRecommendCheckboxListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_scrap -> {
                item.isChecked = !item.isChecked
                viewModel.updateScrap(item.isChecked)
                return true
            }

            R.id.action_update -> {
                Snackbar.make(binding.root, "update", Snackbar.LENGTH_SHORT).show()
                return true
            }

            R.id.action_delete -> {
                Snackbar.make(binding.root, "delete", Snackbar.LENGTH_SHORT).show()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun initAppbar() {
        setSupportActionBar(binding.tbPostDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.postViewModel = viewModel
    }

    private fun setRecommendCheckboxListener() {
        binding.cbUp.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateUpRecommendation(isChecked)
        }
        binding.cbDown.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateDownRecommendation(isChecked)
        }
    }
}
