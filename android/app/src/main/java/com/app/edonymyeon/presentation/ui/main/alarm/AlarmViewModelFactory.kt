package com.app.edonymyeon.presentation.ui.main.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domain.edonymyeon.repository.NotificationRepository

class AlarmViewModelFactory(
    private val notificationRepository: NotificationRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmViewModel(notificationRepository) as T
    }
}
