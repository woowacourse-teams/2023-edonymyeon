package com.app.edonymyeon.presentation.ui.main.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionAmountUiModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionStatisticsUiModel
import com.app.edonymyeon.presentation.uimodel.WriterUiModel
import com.domain.edonymyeon.model.ConsumptionStatistics
import com.domain.edonymyeon.model.Writer
import com.domain.edonymyeon.repository.ConsumptionsRepository
import com.domain.edonymyeon.repository.ProfileRepository
import kotlinx.coroutines.launch

class MyPageViewModel(
    private val profileRepository: ProfileRepository,
    private val consumptionsRepository: ConsumptionsRepository,
) : ViewModel() {
    private val _profile = MutableLiveData<WriterUiModel>()
    val profile: LiveData<WriterUiModel>
        get() = _profile

    private val _consumptions = MutableLiveData<ConsumptionStatisticsUiModel>()
    val consumptions: LiveData<ConsumptionStatisticsUiModel>
        get() = _consumptions

    private val _consumptionOnThisMonth = MutableLiveData<ConsumptionAmountUiModel>()
    val consumptionOnThisMonth: LiveData<ConsumptionAmountUiModel>
        get() = _consumptionOnThisMonth

    init {
        _profile.value = WriterUiModel(0L, "로그인해주세요", null)
        _consumptionOnThisMonth.value = ConsumptionAmountUiModel(0, 0)
    }

    fun getMonthLists(): List<String> =
        _consumptions.value?.toDomain()?.monthRange?.yearMonthList ?: emptyList()

    fun getUserProfile() {
        viewModelScope.launch {
            profileRepository.getProfile()
                .onSuccess {
                    it as Writer
                    _profile.value = it.toUiModel()
                }
                .onFailure {
                    it as CustomThrowable
                }
        }
    }

    fun setConsumptions(period: Int) {
        viewModelScope.launch {
            consumptionsRepository.getConsumptions(period)
                .onSuccess {
                    it as ConsumptionStatistics
                    _consumptions.value = it.toUiModel()
                    _consumptionOnThisMonth.value = it.consumptionAmounts.last().toUiModel()
                }
                .onFailure {
                    it as CustomThrowable
                }
        }
    }
}
