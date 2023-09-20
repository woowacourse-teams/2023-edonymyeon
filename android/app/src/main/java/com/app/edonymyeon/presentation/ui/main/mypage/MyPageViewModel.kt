package com.app.edonymyeon.presentation.ui.main.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.service.client.RetrofitClient
import com.app.edonymyeon.data.service.fcm.FCMToken
import com.app.edonymyeon.data.util.PreferenceUtil
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionAmountUiModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionStatisticsUiModel
import com.app.edonymyeon.presentation.uimodel.NicknameUiModel
import com.app.edonymyeon.presentation.uimodel.WriterUiModel
import com.domain.edonymyeon.model.ConsumptionStatistics
import com.domain.edonymyeon.model.Writer
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
    private val authRepository: AuthRepository,
) : BaseViewModel() {
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
        get() = PreferenceUtil.getValue(AuthLocalDataSource.USER_ACCESS_TOKEN) != null

    fun getMonthLists(): List<String> =
        _consumptions.value?.toDomain()?.monthRange?.yearMonthList ?: emptyList()

    private val _isLogoutSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isLogoutSuccess: LiveData<Boolean>
        get() = _isLogoutSuccess

    fun getUserProfile() {
        viewModelScope.launch(exceptionHandler) {
            profileRepository.getProfile().onSuccess {
                it as Writer
                _profile.value = it.toUiModel()
            }.onFailure {
                it as CustomThrowable
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
            }.onFailure {
                it as CustomThrowable
            }
        }
    }

    fun logout() {
        FCMToken.getFCMToken {
            viewModelScope.launch(exceptionHandler) {
                authRepository.logout(it ?: "").onSuccess {
                    _isLogoutSuccess.value = true
                }.onFailure {
                    it as CustomThrowable
                }
            }
        }
    }

    fun withdraw() {
        viewModelScope.launch(exceptionHandler) {
            profileRepository.withdraw()
                .onFailure {
                    it as CustomThrowable
                }
        }
    }

    fun clearToken() {
        PreferenceUtil.setValue(AuthLocalDataSource.USER_ACCESS_TOKEN, null)
        RetrofitClient.getInstance().clearAccessToken()
    }
}
