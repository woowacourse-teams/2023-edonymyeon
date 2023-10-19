package com.app.edonymyeon.presentation.ui.alarmsetting

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityAlarmSettingBinding
import com.app.edonymyeon.presentation.common.activity.BaseActivity
import com.app.edonymyeon.presentation.util.PermissionUtil
import com.app.edonymyeon.presentation.util.makeSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmSettingActivity : BaseActivity<ActivityAlarmSettingBinding, AlarmSettingViewModel>(
    { ActivityAlarmSettingBinding.inflate(it) },
) {
    override val viewModel: AlarmSettingViewModel by viewModels()
    override val inflater: LayoutInflater by lazy { LayoutInflater.from(this) }

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
