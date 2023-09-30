package com.app.edonymyeon.presentation.ui.profileupdate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.databinding.ActivityProfileUpdateBinding

class ProfileUpdateActivity : AppCompatActivity() {
    private val binding by lazy { ActivityProfileUpdateBinding.inflate(layoutInflater) }

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
