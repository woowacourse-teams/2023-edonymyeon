package com.app.edonymyeon.presentation.ui.postdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostDetailBinding
import app.edonymyeon.databinding.ViewCommentInputBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.datasource.recommend.RecommendRemoteDataSource
import com.app.edonymyeon.data.datasource.report.ReportRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.data.repository.RecommendRepositoryImpl
import com.app.edonymyeon.data.repository.ReportRepositoryImpl
import com.app.edonymyeon.presentation.common.activity.BaseActivity
import com.app.edonymyeon.presentation.common.dialog.LoadingDialog
import com.app.edonymyeon.presentation.ui.login.LoginActivity
import com.app.edonymyeon.presentation.ui.post.PostActivity
import com.app.edonymyeon.presentation.ui.postdetail.adapter.ImageSliderAdapter
import com.app.edonymyeon.presentation.ui.postdetail.dialog.DeleteDialog
import com.app.edonymyeon.presentation.ui.postdetail.dialog.ReportDialog
import com.app.edonymyeon.presentation.ui.posteditor.PostEditorActivity
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.app.edonymyeon.presentation.util.makeSnackbar
import com.app.edonymyeon.presentation.util.makeSnackbarWithEvent

class PostDetailActivity : BaseActivity<ActivityPostDetailBinding, PostDetailViewModel>({
    ActivityPostDetailBinding.inflate(it)
}) {
    private val id: Long by lazy {
        intent.getLongExtra(KEY_POST_ID, -1)
    }

    private val includeBinding: ViewCommentInputBinding by lazy {
        binding.clCommentInput
    }

    override val viewModel: PostDetailViewModel by viewModels {
        PostDetailViewModelFactory(
            PostRepositoryImpl(PostRemoteDataSource()),
            RecommendRepositoryImpl(RecommendRemoteDataSource()),
            ReportRepositoryImpl(ReportRemoteDataSource()),
        )
    }

    override val inflater: LayoutInflater by lazy { LayoutInflater.from(this) }

    private val pickGalleryImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                setGalleryImages(it.data)
            }
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
        setViewLogin()
        getPost()
        setObserver()
        setListener()
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

    private fun setViewLogin() {
        if (!viewModel.isLogin) {
            includeBinding.etComment.isFocusable = false
            includeBinding.etComment.isFocusableInTouchMode = false
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
            binding.clCommentInput.clCommentInput.isVisible = false
        }
    }

    private fun setListener() {
        includeBinding.etComment.setOnClickListener {
            if (!viewModel.isLogin) makeLoginSnackbar()
        }
        includeBinding.ivPostGallery.setOnClickListener {
            if (!viewModel.isLogin) {
                makeLoginSnackbar()
            } else {
                navigateToGallery()
            }
        }
        includeBinding.ivCommentSave.setOnClickListener {
            if (!viewModel.isLogin) makeLoginSnackbar()
        }
        includeBinding.cvContentImage.ivPostGalleryImageRemove.setOnClickListener {
            includeBinding.clGalleryImage.visibility = View.GONE
        }
    }

    private fun navigateToGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        pickGalleryImage.launch(intent)
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

    private fun setGalleryImages(data: Intent?) {
        data?.data?.let { imageUri ->
            includeBinding.cvContentImage.postEditorImage = imageUri.toString()
            includeBinding.clGalleryImage.isVisible = true
        }
    }

    private fun makeLoginSnackbar() {
        binding.root.makeSnackbarWithEvent(
            message = getString(R.string.all_required_login),
            eventTitle = getString(R.string.login_title),
        ) { navigateToLogin() }
    }

    private fun navigateToLogin() {
        startActivity(LoginActivity.newIntent(this))
    }

    companion object {
        const val KEY_POST_ID = "key_post_id"
        fun newIntent(context: Context, postId: Long): Intent {
            return Intent(context, PostDetailActivity::class.java).putExtra(KEY_POST_ID, postId)
        }
    }
}
