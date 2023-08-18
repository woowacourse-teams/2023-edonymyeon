package com.app.edonymyeon.presentation.ui.main.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domain.edonymyeon.repository.AuthRepository
import com.domain.edonymyeon.repository.ConsumptionsRepository
import com.domain.edonymyeon.repository.ProfileRepository

class MyPageViewModelFactory(
    private val profileRepository: ProfileRepository,
    private val consumptionsRepository: ConsumptionsRepository,
    private val authRepository: AuthRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyPageViewModel(profileRepository, consumptionsRepository, authRepository) as T
    }
}
