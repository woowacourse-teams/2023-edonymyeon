package com.app.edonymyeon.presentation.ui.alarmsetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.edonymyeon.presentation.ui.mypost.MyPostViewModel
import com.domain.edonymyeon.repository.ProfileRepository

class AlarmSettingViewModelFactory(private val repository: ProfileRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyPostViewModel(repository) as T
    }
}
