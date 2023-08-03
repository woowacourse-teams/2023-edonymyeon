package com.app.edonymyeon.presentation.ui.main.mypage

import android.util.Log
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
import com.domain.edonymyeon.model.Writer
import com.domain.edonymyeon.repository.ProfileRepository
import kotlinx.coroutines.launch
import java.time.YearMonth
import kotlin.random.Random

class MyPageViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _profile = MutableLiveData<WriterUiModel>()
    val profile: LiveData<WriterUiModel>
        get() = _profile

    private val _consumptions = MutableLiveData<ConsumptionStatisticsUiModel>()
    val consumptions: LiveData<ConsumptionStatisticsUiModel>
        get() = _consumptions

    fun getMonthLists(): List<String> =
        _consumptions.value?.toDomain()?.monthRange?.yearMonthList ?: emptyList()

    fun getUserProfile() {
        viewModelScope.launch {
            profileRepository.getProfile()
                .onSuccess {
                    it as Writer
                    Log.d("MyPageViewModel", "getUserProfile: ${it.toUiModel()}")
                    _profile.value = it.toUiModel()
                }
                .onFailure {
                    it as CustomThrowable
                    Log.d("MyPageViewModel", "getUserProfile: ${it.message}")
                    when (it.code) {
                    }
                }
        }
    }

    fun setConsumptions() {
        _consumptions.value = ConsumptionStatisticsUiModel(
            startMonth = YearMonth.parse("2023-02"),
            endMonth = YearMonth.parse("2023-07"),
            consumptionAmounts = List(6) {
                ConsumptionAmountUiModel(
                    Random.nextInt(10000, 100000),
                    Random.nextInt(10000, 100000),
                )
            },
        )
    }
}
