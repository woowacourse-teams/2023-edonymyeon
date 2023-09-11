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
import com.app.edonymyeon.data.datasource.profile.ProfileRemoteDataSource
import com.app.edonymyeon.data.repository.ProfileRepositoryImpl
import com.app.edonymyeon.presentation.common.activity.BaseActivity
import com.app.edonymyeon.presentation.util.PermissionUtil
import com.app.edonymyeon.presentation.util.makeSnackbar

class AlarmSettingActivity : BaseActivity<ActivityAlarmSettingBinding, AlarmSettingViewModel>(
    {
        ActivityAlarmSettingBinding.inflate(it)
    },
) {

    override val viewModel: AlarmSettingViewModel by viewModels {
        AlarmSettingViewModelFactory(
            ProfileRepositoryImpl(ProfileRemoteDataSource()), // 수정해야함
        )
    }
    override val inflater: LayoutInflater by lazy { LayoutInflater.from(this) }

    // 권한으로 최상단 push alarm 수신 여부를 설정한다.
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted.not()) {
            binding.switchAlarmSetting.isChecked = false
            binding.root.makeSnackbar(getString(R.string.alarm_setting_request_alarm_permission))
        } else {
            // viewModel.setPushAlarmSetting
            // viewModel.getPushAlarmSetting?
            setAlarmSettingIsClickable(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBinding()
        setNavigationClickListener()
        setObserver()
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
                    // viewModel.setPushAlarmSetting
                    // viewModel.getPushAlarmSetting
                    setAlarmSettingIsClickable(true)
                } else {
                    setAlarmSettingIsClickable(false)
                }
            }
        }
    }

    private fun setAlarmSettingIsClickable(isClickable: Boolean) {
        binding.switchTenReaction.isClickable = isClickable
        binding.switchOneReaction.isClickable = isClickable
        binding.switchConfirmConsumption.isClickable = isClickable
    }

    private fun setNotificationGrantedUiState() {
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
