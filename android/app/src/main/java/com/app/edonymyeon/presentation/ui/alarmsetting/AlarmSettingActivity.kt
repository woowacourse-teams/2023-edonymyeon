package com.app.edonymyeon.presentation.ui.alarmsetting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.databinding.ActivityAlarmSettingBinding

class AlarmSettingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAlarmSettingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}
