package com.app.edonymyeon.presentation.ui.main.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.service.fcm.FCMToken
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionAmountUiModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionStatisticsUiModel
import com.app.edonymyeon.presentation.uimodel.NicknameUiModel
import com.app.edonymyeon.presentation.uimodel.WriterUiModel
import com.app.edonymyeon.presentation.util.onFailureWithApiException
import com.domain.edonymyeon.model.ConsumptionStatistics
import com.domain.edonymyeon.repository.AuthRepository
import com.domain.edonymyeon.repository.ConsumptionsRepository
import com.domain.edonymyeon.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val consumptionsRepository: ConsumptionsRepository,
    authRepository: AuthRepository,
) : BaseViewModel(authRepository) {
    private val _profile = MutableLiveData<WriterUiModel>()
    val profile: LiveData<WriterUiModel>
        get() = _profile

    private val _consumptions = MutableLiveData<ConsumptionStatisticsUiModel>()
    val consumptions: LiveData<ConsumptionStatisticsUiModel>
        get() = _consumptions

    private val _consumptionOnThisMonth = MutableLiveData<ConsumptionAmountUiModel>()
    val consumptionOnThisMonth: LiveData<ConsumptionAmountUiModel>
        get() = _consumptionOnThisMonth

    val isLogin: Boolean
        get() = authRepository.getToken() != null

    fun getMonthLists(): List<String> =
        _consumptions.value?.toDomain()?.monthRange?.yearMonthList ?: emptyList()

    private val _isLogoutSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isLogoutSuccess: LiveData<Boolean>
        get() = _isLogoutSuccess

    fun getUserProfile() {
        viewModelScope.launch(exceptionHandler) {
            profileRepository.getProfile().onSuccess {
                _profile.value = it.toUiModel()
            }.onFailureWithApiException {
                throw it
            }
        }
    }

    fun setNoUserState(defaultNickname: NicknameUiModel) {
        _profile.value = WriterUiModel(0L, defaultNickname, null)
        _consumptionOnThisMonth.value = ConsumptionAmountUiModel(0, 0)
    }

    fun setConsumptions(period: Int) {
        viewModelScope.launch(exceptionHandler) {
            consumptionsRepository.getConsumptions(period).onSuccess {
                it as ConsumptionStatistics
                _consumptions.value = it.toUiModel()
                _consumptionOnThisMonth.value = it.consumptionAmounts.last().toUiModel()
            }.onFailureWithApiException {
                throw it
            }
        }
    }

    fun logout() {
        FCMToken.getFCMToken {
            viewModelScope.launch(exceptionHandler) {
                authRepository.logout(it ?: "").onSuccess {
                    _isLogoutSuccess.value = true
                }.onFailureWithApiException {
                    throw it
                }
            }
        }
    }

    fun withdraw() {
        viewModelScope.launch(exceptionHandler) {
            profileRepository.withdraw()
                .onFailureWithApiException {
                    throw it
                }
        }
    }

    fun clearToken() {
        authRepository.setToken(null)
    }
}
