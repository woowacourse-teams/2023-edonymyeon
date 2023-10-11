package com.app.edonymyeon.presentation.ui.profileupdate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import app.edonymyeon.databinding.ActivityProfileUpdateBinding
import com.app.edonymyeon.presentation.common.activity.BaseActivity
import com.app.edonymyeon.presentation.uimodel.WriterUiModel
import com.app.edonymyeon.presentation.util.getParcelableExtraCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileUpdateActivity : BaseActivity<ActivityProfileUpdateBinding, ProfileUpdateViewModel>(
    { ActivityProfileUpdateBinding.inflate(it) },
) {
    override val viewModel: ProfileUpdateViewModel by viewModels()
    override val inflater: LayoutInflater by lazy { LayoutInflater.from(this) }

    private val originalProfile by lazy {
        intent.getParcelableExtraCompat(KEY_PROFILE) as? WriterUiModel
            ?: throw IllegalArgumentException()
    }

    private val pickMultipleImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.setNewProfileImage(uri.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBinding()
        setOriginalProfile()
        setImageButtonsClickListener()
        setUpdateProfileButtonClickListener()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setOriginalProfile() {
        viewModel.initOriginalProfile(originalProfile)
    }

    private fun setImageButtonsClickListener() {
        binding.btnImageUpload.setOnClickListener {
            navigateToGallery()
        }
        binding.btnImageDelete.setOnClickListener {
            viewModel.deleteProfileImage()
        }
    }

    private fun setUpdateProfileButtonClickListener() {
        binding.btnUpdate.setOnClickListener {
            viewModel.updateProfile(this, binding.etNickname.text.toString())
        }
    }

    private fun navigateToGallery() {
        pickMultipleImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    companion object {
        private const val KEY_PROFILE = "key_profile"

        fun newIntent(context: Context, profile: WriterUiModel): Intent {
            return Intent(context, ProfileUpdateActivity::class.java).apply {
                putExtra(KEY_PROFILE, profile)
            }
        }
    }
}
