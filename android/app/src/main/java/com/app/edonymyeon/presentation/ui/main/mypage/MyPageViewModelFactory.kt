package com.app.edonymyeon.presentation.ui.main.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domain.edonymyeon.repository.ConsumptionsRepository
import com.domain.edonymyeon.repository.ProfileRepository

class MyPageViewModelFactory(
    private val profileRepository: ProfileRepository,
    private val consumptionsRepository: ConsumptionsRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyPageViewModel(profileRepository, consumptionsRepository) as T
    }
}
