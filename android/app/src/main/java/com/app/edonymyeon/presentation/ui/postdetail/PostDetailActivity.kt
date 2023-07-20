package com.app.edonymyeon.presentation.ui.postdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostDetailBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.presentation.ui.postdetail.adapter.ImageSliderAdapter
import com.app.edonymyeon.presentation.ui.postdetail.dialog.DeleteDialog
import com.app.edonymyeon.presentation.ui.posteditor.PostEditorActivity
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.google.android.material.snackbar.Snackbar

class PostDetailActivity : AppCompatActivity() {
    private val binding: ActivityPostDetailBinding by lazy {
        ActivityPostDetailBinding.inflate(layoutInflater)
    }

    private val viewModel: PostDetailViewModel by viewModels {
        PostDetailViewModelFactory(
            23L,
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

        setImageIndicators()

        viewModel.post.observe(this) {
            setImageSlider(it)
        }

        viewModel.reactionCount.observe(this) {
            binding.postReactionCtv.reactionCount = it
        }
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
                startActivity(PostEditorActivity.newIntent(this, PostEditorActivity.UPDATE_CODE))
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

    private fun setImageSlider(post: PostUiModel) {
        binding.vpImageSlider.offscreenPageLimit = 1
        binding.vpImageSlider.adapter =
            ImageSliderAdapter(post.images)
        binding.vpImageSlider.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateCurrentIndicator(position)
                }
            },
        )
    }

    private fun setImageIndicators() {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply { setMargins(8, 0, 8, 0) }

        addIndicatorViews(params)
        updateCurrentIndicator(0)
    }

    private fun addIndicatorViews(params: LinearLayout.LayoutParams) {
        List<ImageView>(binding.vpImageSlider.adapter?.itemCount ?: 0) {
            ImageView(this).apply {
                setImageResource(R.drawable.ic_indicator_focus_off)
                layoutParams = params
            }.also { indicatorView ->
                binding.llIndicators.addView(indicatorView)
            }
        }
    }

    private fun updateCurrentIndicator(position: Int) {
        for (i in 0 until binding.llIndicators.childCount) {
            val indicatorView = binding.llIndicators.getChildAt(i) as ImageView
            if (i == position) {
                indicatorView.setImageResource(R.drawable.ic_indicator_focus_on)
            } else {
                indicatorView.setImageResource(R.drawable.ic_indicator_focus_off)
            }
        }
    }

    companion object {
        private const val KEY_POST_ID = "key_post_id"
        fun newIntent(context: Context, postId: Long): Intent {
            return Intent(context, PostDetailActivity::class.java).putExtra(KEY_POST_ID, postId)
        }
    }
}
