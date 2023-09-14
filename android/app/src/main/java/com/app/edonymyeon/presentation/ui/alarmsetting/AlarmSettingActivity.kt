package com.app.edonymyeon.presentation.ui.alarmsetting

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityAlarmSettingBinding
import com.app.edonymyeon.data.datasource.preference.PreferenceRemoteDataSource
import com.app.edonymyeon.data.repository.PreferenceRepositoryImpl
import com.app.edonymyeon.presentation.util.PermissionUtil
import com.app.edonymyeon.presentation.util.makeSnackbar

class AlarmSettingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAlarmSettingBinding.inflate(layoutInflater)
    }

    private val viewModel: AlarmSettingViewModel by viewModels {
        AlarmSettingViewModelFactory(
            PreferenceRepositoryImpl(PreferenceRemoteDataSource()),
        )
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted.not()) {
            binding.root.makeSnackbar(getString(R.string.alarm_setting_request_alarm_permission))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBinding()
        setNavigationClickListener()
        setObserver()

        viewModel.getPushAlarmSetting()
    }

    override fun onStart() {
        super.onStart()
        if (PermissionUtil.isNotificationGranted(this).not()) {
            binding.switchAlarmSetting.isChecked = false
        }
    }

    private fun initBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    @SuppressLint("InlinedApi")
    private fun setObserver() {
        with(viewModel) {
            isPushAlarmOn.observe(this@AlarmSettingActivity) {
                if (it) {
                    if (PermissionUtil.isNotificationGranted(this@AlarmSettingActivity).not()) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        return@observe
                    }
                }
            }
        }
    }

    private fun setNavigationClickListener() {
        binding.tbAlarmSetting.setNavigationOnClickListener {
            finish()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AlarmSettingActivity::class.java)
        }
    }
}
