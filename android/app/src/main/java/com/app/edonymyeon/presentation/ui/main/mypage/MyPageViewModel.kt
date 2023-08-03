package com.app.edonymyeon.presentation.ui.main.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.presentation.uimodel.ConsumptionAmountUiModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionStatisticsUiModel
import java.time.YearMonth
import kotlin.random.Random

class MyPageViewModel : ViewModel() {
    private val _consumptions = MutableLiveData<ConsumptionStatisticsUiModel>()
    val consumptions: LiveData<ConsumptionStatisticsUiModel>
        get() = _consumptions

    fun getMonthLists(): List<String> =
        _consumptions.value?.toDomain()?.monthRange?.yearMonthList ?: emptyList()

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
