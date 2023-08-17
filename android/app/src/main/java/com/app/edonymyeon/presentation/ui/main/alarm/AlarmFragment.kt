package com.app.edonymyeon.presentation.ui.main.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.edonymyeon.databinding.FragmentAlarmBinding
import com.app.edonymyeon.data.datasource.notification.NotificationRemoteDataSource
import com.app.edonymyeon.data.repository.NotificationRepositoryImpl

class AlarmFragment : Fragment() {
    private val binding: FragmentAlarmBinding by lazy {
        FragmentAlarmBinding.inflate(layoutInflater)
    }

    private val viewModel: AlarmViewModel by viewModels {
        AlarmViewModelFactory(
            NotificationRepositoryImpl(
                NotificationRemoteDataSource(),
            ),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        initBinding()
        return binding.root
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            viewModel.checkLogin()
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        viewModel.checkLogin()
    }

    private fun setObserver() {
        with(viewModel) {
        }
    }
}
