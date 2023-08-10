package com.app.edonymyeon.presentation.ui.main.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import app.edonymyeon.databinding.FragmentAlarmBinding
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.util.PreferenceUtil

class AlarmFragment : Fragment() {
    private val binding: FragmentAlarmBinding by lazy {
        FragmentAlarmBinding.inflate(layoutInflater)
    }

    private val isLogin: Boolean
        get() = PreferenceUtil.getValue(AuthLocalDataSource.USER_ACCESS_TOKEN) != null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isLogin) {
            binding.tvRequiredLogin.isVisible = false
        }
    }
}
