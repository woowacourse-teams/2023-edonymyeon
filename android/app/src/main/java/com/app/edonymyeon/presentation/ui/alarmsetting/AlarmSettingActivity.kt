package com.app.edonymyeon.presentation.ui.alarmsetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.databinding.ActivityAlarmSettingBinding
import com.app.edonymyeon.data.datasource.profile.ProfileRemoteDataSource
import com.app.edonymyeon.data.repository.ProfileRepositoryImpl

class AlarmSettingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAlarmSettingBinding.inflate(layoutInflater)
    }

    private val viewModel: AlarmSettingViewModel by viewModels {
        AlarmSettingViewModelFactory(
            ProfileRepositoryImpl(ProfileRemoteDataSource()), // 수정해야함
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AlarmSettingActivity::class.java)
        }
    }
}
