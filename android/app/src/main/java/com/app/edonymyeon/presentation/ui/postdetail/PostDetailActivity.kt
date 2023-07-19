package com.app.edonymyeon.presentation.ui.postdetail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostDetailBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.presentation.ui.postdetail.adapter.ImageSliderAdapter
import com.google.android.material.snackbar.Snackbar

class PostDetailActivity : AppCompatActivity() {
    private val binding: ActivityPostDetailBinding by lazy {
        ActivityPostDetailBinding.inflate(layoutInflater)
    }

    private val viewModel: PostDetailViewModel by lazy {
        PostDetailViewModelFactory(PostRepositoryImpl(PostRemoteDataSource())).create(
            PostDetailViewModel::class.java,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initBinding()
        initAppbar()

        setRecommendCheckboxListener()
        setImageSlider()
        setImageIndicators()
        viewModel.getPostDetail(0L)
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

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.postViewModel = viewModel
    }

    private fun initAppbar() {
        setSupportActionBar(binding.tbPostDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    private fun setRecommendCheckboxListener() {
        binding.cbUp.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateUpRecommendation(isChecked)
        }
        binding.cbDown.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateDownRecommendation(isChecked)
        }
    }

    private fun setImageSlider() {
        binding.vpImageSlider.offscreenPageLimit = 1
        binding.vpImageSlider.adapter =
            ImageSliderAdapter(viewModel.post.value?.images ?: emptyList())
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
}
