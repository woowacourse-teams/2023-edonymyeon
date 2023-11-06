package com.app.edonymyeon.presentation.ui.posteditor

import android.Manifest
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostEditorBinding
import com.app.edonymyeon.presentation.common.activity.BaseActivity
import com.app.edonymyeon.presentation.common.activityutil.hideKeyboard
import com.app.edonymyeon.presentation.common.dialog.LoadingDialog
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity
import com.app.edonymyeon.presentation.ui.posteditor.adapter.PostEditorImagesAdapter
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.app.edonymyeon.presentation.util.getParcelableExtraCompat
import com.app.edonymyeon.presentation.util.makeSnackbar
import com.app.edonymyeon.presentation.util.makeSnackbarWithEvent
import com.domain.edonymyeon.model.PostEditor
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PostEditorActivity : BaseActivity<ActivityPostEditorBinding, PostEditorViewModel>(
    { ActivityPostEditorBinding.inflate(it) },
) {

    private var cameraUri: Uri? = null

    private var price = ""

    override val viewModel: PostEditorViewModel by viewModels()

    private val adapter: PostEditorImagesAdapter by lazy {
        PostEditorImagesAdapter(::deleteImages)
    }

    override val inflater: LayoutInflater by lazy { LayoutInflater.from(this) }

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(getString(R.string.post_editor_loading_message))
    }

    private val pickMultipleImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                setGalleryImages(it.data)
            }
        }

    private val takeCameraImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) setCameraImage(cameraUri)
        }

    private val permissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions: Map<String, Boolean> ->
        permissions.entries.forEach { entry ->
            val isGranted = entry.value
            if (!isGranted) {
                showPermissionSnackbar()
            } else {
                navigateToCamera()
            }
        }
    }

    private val originActivityKey by lazy {
        intent.getIntExtra(KEY_POST_EDITOR_CHECK, 0)
    }

    private val post by lazy {
        intent.getParcelableExtraCompat(KEY_POST_EDITOR_POST) as? PostUiModel
    }

    private val textWatcher by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s.toString()) && s.toString() != price) {
                    viewModel.checkPriceValidate(
                        s.toString().replace(",", ""),
                        start,
                        start + count,
                        count,
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initBinding()
        initAdapter()
        setAdapter()
        setObserver()
        setPostIfUpdate()
        setClickListener()
        setPriceChangedListener()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        hideKeyboard()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_post_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_post_save -> {
                hideKeyboard()
                item.isEnabled = false
                viewModel.checkTitleValidate(binding.etPostTitle.text.toString())
                true
            }

            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initAdapter() {
        setSupportActionBar(binding.tbPostEditor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    private fun setAdapter() {
        binding.rvPostEditorImages.adapter = adapter
    }

    private fun setObserver() {
        viewModel.isPostPriceValid.observe(this) { isValid ->
            if (!isValid) {
                binding.root.makeSnackbar(this.getString(R.string.dialog_input_price_error_message))
                binding.etPostPrice.removeTextChangedListener(textWatcher)
                binding.etPostPrice.setText(price)
                binding.etPostPrice.setSelection(binding.etPostPrice.text.length)
                binding.etPostPrice.addTextChangedListener(textWatcher)
            } else {
                price =
                    getString(
                        R.string.all_price,
                        binding.etPostPrice.text.toString().replace(",", "").toInt(),
                    )
                binding.etPostPrice.setText(price)
                binding.etPostPrice.setSelection(price.length)
            }
        }
        viewModel.isUpdateAble.observe(this) { isAble ->
            if (isAble) {
                savePost()
            } else {
                showSnackbarForMissingTitle()
                binding.tbPostEditor.menu.findItem(R.id.action_post_save).isEnabled = true
            }
        }

        viewModel.postId.observe(this) {
            setResult(RESULT_RELOAD_CODE)
            navigateToDetail()
        }
        viewModel.galleryImages.observe(this) { images ->
            adapter.setImages(images)
        }
    }

    private fun setPostIfUpdate() {
        if (originActivityKey == UPDATE_CODE) {
            post?.let { viewModel.initViewModelOnUpdate(it) }
        }
    }

    private fun setClickListener() {
        binding.ivPostCamera.setOnClickListener { requestPermission() }
        binding.ivPostGallery.setOnClickListener { navigateToGallery() }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val backgroundPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            val permissionList = requestPermissions.toMutableList()
            if (permissionList.contains(backgroundPermission)) {
                permissionList.remove(backgroundPermission)
            }
            val permissionArray = permissionList.toTypedArray()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                navigateToCamera()
                return
            }
            permissionRequestLauncher.launch(permissionArray)
        } else {
            permissionRequestLauncher.launch(requestPermissions)
        }
    }

    private fun navigateToCamera() {
        val photoFile = File.createTempFile("IMG_", ".jpg", this.cacheDir)
        cameraUri = FileProvider.getUriForFile(this, "$packageName.provider", photoFile)
        takeCameraImage.launch(cameraUri)
    }

    private fun navigateToGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pickMultipleImage.launch(intent)
    }

    private fun savePost() {
        loadingDialog.show(supportFragmentManager, "LoadingDialog")
        val postTitle = binding.etPostTitle.text.toString()
        val postContent = binding.etPostContent.text.toString().ifBlank { "" }
        val postPrice = binding.etPostPrice.text.toString().replace(",", "").toInt()
        val postEditor = PostEditor(postTitle, postContent, postPrice)
        when (originActivityKey) {
            POST_CODE -> viewModel.savePost(this, postEditor)
            UPDATE_CODE -> post?.let {
                viewModel.updatePost(this, it.id, postEditor)
            }
        }
    }

    private fun showSnackbarForMissingTitle() {
        binding.root.makeSnackbar(getString(R.string.post_editor_snackbar_missing_title_message))
    }

    private fun showPermissionSnackbar() {
        binding.root.makeSnackbarWithEvent(
            getString(R.string.post_editor_check_permission),
            getString(R.string.post_editor_snackbar_ok_message),
        ) {
            openAppSetting()
        }
    }

    private fun openAppSetting() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun deleteImages(image: String) {
        viewModel.deleteSelectedImages(image)
    }

    private fun setGalleryImages(data: Intent?) {
        data?.clipData?.let { clipData ->
            val count = clipData.itemCount
            val currentImageCount = viewModel.galleryImages.value?.size ?: 0
            when {
                checkImageCountLimit(count, MAX_IMAGES_COUNT).not() -> return
                checkImageCountLimit(count, MAX_IMAGES_COUNT - currentImageCount).not() -> return
                else -> addSelectedImagesFromClipData(count, clipData)
            }
        } ?: addSelectedImageFromUri(data)
        setAdapter()
    }

    private fun setCameraImage(imageUri: Uri?) {
        viewModel.addSelectedImages(imageUri.toString())
    }

    private fun addSelectedImagesFromClipData(count: Int, clipData: ClipData) {
        for (i in 0 until count) {
            val imageUri = clipData.getItemAt(i).uri
            viewModel.addSelectedImages(imageUri.toString())
        }
    }

    private fun addSelectedImageFromUri(data: Intent?) {
        data?.data?.let { imageUri ->
            viewModel.addSelectedImages(imageUri.toString())
        }
    }

    private fun checkImageCountLimit(count: Int, limitCount: Int): Boolean {
        if (count > limitCount) {
            binding.clPostEditor.makeSnackbar(getString(R.string.post_editor_image_limit_message))
            return false
        }
        return true
    }

    private fun navigateToDetail() {
        startActivity(PostDetailActivity.newIntent(this, viewModel.postId.value ?: -1))
        finish()
    }

    private fun setPriceChangedListener() {
        binding.etPostPrice.addTextChangedListener(textWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_CAMERA_URI, cameraUri)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?,
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        cameraUri = savedInstanceState?.getParcelableExtraCompat(KEY_CAMERA_URI) as Uri?
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            cameraUri?.let { uri ->
                val contentResolver = applicationContext.contentResolver
                contentResolver.delete(uri, null, null)
            }
        }
    }

    companion object {
        private const val MAX_IMAGES_COUNT = 10
        private const val KEY_POST_EDITOR_CHECK = "key_post_editor_check"
        private const val KEY_POST_EDITOR_POST = "key_post_editor_post"
        private const val KEY_CAMERA_URI = "cameraUri"
        const val POST_CODE = 2000
        const val UPDATE_CODE = 3000
        const val RESULT_RELOAD_CODE = 1001

        private val requestPermissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        fun newIntent(context: Context, code: Int): Intent {
            return Intent(context, PostEditorActivity::class.java).apply {
                putExtra(KEY_POST_EDITOR_CHECK, code)
            }
        }

        fun newIntent(context: Context, post: PostUiModel, code: Int): Intent {
            return Intent(context, PostEditorActivity::class.java).apply {
                putExtra(KEY_POST_EDITOR_CHECK, code)
                putExtra(KEY_POST_EDITOR_POST, post)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
    }
}
