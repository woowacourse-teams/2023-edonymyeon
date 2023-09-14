package com.app.edonymyeon.presentation.ui.alarmsetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domain.edonymyeon.repository.PreferenceRepository

class AlarmSettingViewModelFactory(private val repository: PreferenceRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmSettingViewModel(repository) as T
    }
}
