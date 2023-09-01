package com.app.edonymyeon.presentation.ui.postdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostDetailBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.datasource.recommend.RecommendRemoteDataSource
import com.app.edonymyeon.data.datasource.report.ReportRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.data.repository.RecommendRepositoryImpl
import com.app.edonymyeon.data.repository.ReportRepositoryImpl
import com.app.edonymyeon.presentation.common.dialog.LoadingDialog
import com.app.edonymyeon.presentation.ui.post.PostActivity
import com.app.edonymyeon.presentation.ui.postdetail.adapter.ImageSliderAdapter
import com.app.edonymyeon.presentation.ui.postdetail.dialog.DeleteDialog
import com.app.edonymyeon.presentation.ui.postdetail.dialog.ReportDialog
import com.app.edonymyeon.presentation.ui.posteditor.PostEditorActivity
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.app.edonymyeon.presentation.util.makeSnackbar

class PostDetailActivity : AppCompatActivity() {
    private val id: Long by lazy {
        intent.getLongExtra(KEY_POST_ID, -1)
    }

    private val binding: ActivityPostDetailBinding by lazy {
        ActivityPostDetailBinding.inflate(layoutInflater)
    }

    private val viewModel: PostDetailViewModel by viewModels<PostDetailViewModel> {
        PostDetailViewModelFactory(
            PostRepositoryImpl(PostRemoteDataSource()),
            RecommendRepositoryImpl(RecommendRemoteDataSource()),
            ReportRepositoryImpl(ReportRemoteDataSource()),
        )
    }

    private val reportDialog: ReportDialog by lazy {
        ReportDialog(id, viewModel)
    }

    private val deleteDialog: DeleteDialog by lazy {
        DeleteDialog {
            viewModel.deletePost(id)
            deleteDialog.dismiss()
            startActivity(PostActivity.newIntent(this))
            finish()
        }
    }

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(getString(R.string.post_detail_loading))
    }

    private val isMyPost: Boolean
        get() = viewModel.post.value?.isWriter == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initBinding()
        initAppbar()
        setViewByLoginStatus()
        getPost()
        setObserver()
        setRecommendationCheckedListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post_detail, menu)
        hideReportForWriter(menu)
        hideMenusForWriter(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_report -> {
                reportDialog.show(supportFragmentManager, "ReportDialog")
                true
            }

            R.id.action_update -> {
                val post = viewModel.post.value ?: return false
                startActivity(
                    PostEditorActivity.newIntent(
                        this,
                        post,
                        PostEditorActivity.UPDATE_CODE,
                    ),
                )
                finish()
                true
            }

            R.id.action_delete -> {
                deleteDialog.show(supportFragmentManager, "DeleteDialog")
                true
            }

            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun hideReportForWriter(menu: Menu?) {
        viewModel.post.observe(this) { post ->
            if (post.isWriter) {
                menu?.findItem(R.id.action_report)?.isVisible = false
            }
        }
    }

    private fun hideMenusForWriter(menu: Menu?) {
        listOf(
            menu?.findItem(R.id.action_update),
            menu?.findItem(R.id.action_delete),
        ).forEach {
            viewModel.post.observe(this) { post ->
                if (!post.isWriter) {
                    it?.isVisible = false
                }
            }
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

    private fun setViewByLoginStatus() {
        if (!viewModel.isLogin) {
            binding.clCommentInput.isVisible = false
        }
    }

    private fun getPost() {
        viewModel.getPostDetail(id)
    }

    private fun setObserver() {
        viewModel.isLoadingSuccess.observe(this) { isLoadingSuccess ->
            if (!isLoadingSuccess) {
                if (!loadingDialog.isAdded) {
                    loadingDialog.show(supportFragmentManager, "loadingDialog")
                }
            } else {
                loadingDialog.dismiss()
            }
        }
        viewModel.post.observe(this) { post ->
            if (post.images.isNotEmpty()) {
                binding.ivDefaultImage.isVisible = false
                setImageSlider(post)
                setImageIndicators()
            }
            hideInputCommentForWriter(post)
        }

        viewModel.reactionCount.observe(this) {
            binding.postReactionCtv.reactionCount = it
        }

        // 추천/비추천 체크박스 클릭 시 활성화/비활성화
        viewModel.isRecommendationRequestDone.observe(this) {
            binding.cbUp.isEnabled = it
            binding.cbDown.isEnabled = it
        }
    }

    private fun hideInputCommentForWriter(post: PostUiModel) {
        if (post.isWriter) {
            binding.clCommentInput.isVisible = false
        }
    }

    private fun setRecommendationCheckedListener() {
        binding.cbUp.setOnCheckedChangeListener { _, isChecked ->
            if (invalidateRecommendation(binding.cbUp)) return@setOnCheckedChangeListener
            viewModel.updateRecommendationUi(id, isChecked, true)
        }
        binding.cbDown.setOnCheckedChangeListener { _, isChecked ->
            if (invalidateRecommendation(binding.cbDown)) return@setOnCheckedChangeListener
            viewModel.updateRecommendationUi(id, isChecked, false)
        }
    }

    private fun invalidateRecommendation(checkbox: CheckBox): Boolean {
        if (!viewModel.isLogin) {
            binding.root.makeSnackbar(getString(R.string.post_detail_login_required))
            checkbox.isChecked = false
            return true
        }
        if (isMyPost) {
            binding.root.makeSnackbar(getString(R.string.post_detail_writer_cant_recommend))
            checkbox.isChecked = false
            return true
        }
        return false
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
                setImageResource(R.drawable.ic_bcc4d8_indicator_focus_off)
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
                indicatorView.setImageResource(R.drawable.ic_576b9e_indicator_focus_on)
            } else {
                indicatorView.setImageResource(R.drawable.ic_bcc4d8_indicator_focus_off)
            }
        }
    }

    companion object {
        const val KEY_POST_ID = "key_post_id"
        fun newIntent(context: Context, postId: Long): Intent {
            return Intent(context, PostDetailActivity::class.java).putExtra(KEY_POST_ID, postId)
        }
    }
}
