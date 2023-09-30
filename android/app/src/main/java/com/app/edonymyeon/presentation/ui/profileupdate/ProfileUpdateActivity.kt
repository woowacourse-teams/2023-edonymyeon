package com.app.edonymyeon.presentation.ui.profileupdate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import app.edonymyeon.databinding.ActivityProfileUpdateBinding
import com.app.edonymyeon.presentation.common.activity.BaseActivity

class ProfileUpdateActivity : BaseActivity<ActivityProfileUpdateBinding, ProfileUpdateViewModel>(
    { ActivityProfileUpdateBinding.inflate(it) },
) {
    override val viewModel: ProfileUpdateViewModel by viewModels()

    override val inflater: LayoutInflater by lazy { LayoutInflater.from(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileUpdateActivity::class.java)
        }
    }
}
