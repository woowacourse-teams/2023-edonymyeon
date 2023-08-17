package com.app.edonymyeon.presentation.ui.posteditor

import android.Manifest
import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityPostEditorBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.presentation.common.dialog.LoadingDialog
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity.Companion.KEY_POST_ID
import com.app.edonymyeon.presentation.ui.posteditor.adapter.PostEditorImagesAdapter
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.app.edonymyeon.presentation.util.getParcelableExtraCompat
import com.app.edonymyeon.presentation.util.makeSnackbar
import com.app.edonymyeon.presentation.util.makeSnackbarWithEvent
import com.google.android.material.snackbar.Snackbar

class PostEditorActivity : AppCompatActivity() {

    private val viewModel: PostEditorViewModel by viewModels {
        PostEditorViewModelFactory(application, PostRepositoryImpl(PostRemoteDataSource()))
    }
    private val adapter: PostEditorImagesAdapter by lazy {
        PostEditorImagesAdapter(::deleteImages)
    }

    private val binding: ActivityPostEditorBinding by lazy {
        ActivityPostEditorBinding.inflate(layoutInflater)
    }

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
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                setCameraImage(bitmap)
            }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initBinding()
        initAdapter()
        setAdapter()
        setObserver()
        setPostIfUpdate()
        setClickListener()
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
                binding.etPostPrice.setText("")
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
            setResult(
                RESULT_RELOAD_CODE,
                Intent().putExtra(KEY_POST_ID, viewModel.postId.value ?: -1),
            )
            finish()
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
        takeCameraImage.launch(null)
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
        val postPrice = binding.etPostPrice.text.toString().toInt()

        when (originActivityKey) {
            POST_CODE -> viewModel.savePost(postTitle, postContent, postPrice)
            UPDATE_CODE -> post?.let {
                viewModel.updatePost(
                    it.id,
                    postTitle,
                    postContent,
                    postPrice,
                )
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

    private fun setCameraImage(bitmap: Bitmap) {
        val imageUri = getImageUri(bitmap)
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

    private fun getImageUri(bitmap: Bitmap): Uri? {
        val resolver = applicationContext.contentResolver
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?.let { imageUri ->
                val outputStream = resolver.openOutputStream(imageUri)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                }
                return imageUri
            }
        return null
    }

    private fun checkImageCountLimit(count: Int, limitCount: Int): Boolean {
        if (count > limitCount) {
            Snackbar.make(
                binding.clPostEditor,
                getString(R.string.post_editor_image_limit_message),
                Snackbar.LENGTH_SHORT,
            ).show()
            return false
        }
        return true
    }

    /*private fun navigateToDetail() {
        startActivity(PostDetailActivity.newIntent(this, viewModel.postId.value ?: -1))
        finish()
    }*/

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    companion object {
        private const val MAX_IMAGES_COUNT = 10
        private const val KEY_POST_EDITOR_CHECK = "key_post_editor_check"
        private const val KEY_POST_EDITOR_POST = "key_post_editor_post"
        const val POST_CODE = 2000
        const val UPDATE_CODE = 3000
        const val RESULT_RELOAD_CODE = 1001

        private val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "ImageTitle")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

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
