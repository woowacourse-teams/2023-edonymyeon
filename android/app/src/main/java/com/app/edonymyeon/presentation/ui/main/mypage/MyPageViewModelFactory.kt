package com.app.edonymyeon.presentation.ui.main.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domain.edonymyeon.repository.ProfileRepository

class MyPageViewModelFactory(
    private val profileRepository: ProfileRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyPageViewModel(profileRepository) as T
    }
}
