package com.app.edonymyeon.presentation.ui.main.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.presentation.uimodel.ConsumptionUiModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionsUiModel
import java.time.YearMonth
import kotlin.random.Random

class MyPageViewModel : ViewModel() {
    private val _consumptions = MutableLiveData<ConsumptionsUiModel>()
    val consumptions: LiveData<ConsumptionsUiModel>
        get() = _consumptions

    fun getMonthLists(): List<String> = _consumptions.value?.toDomain()?.yearMonthList ?: emptyList()

    fun setConsumptions() {
        _consumptions.value = ConsumptionsUiModel(
            startMonth = YearMonth.parse("2023-02"),
            endMonth = YearMonth.parse("2023-07"),
            consumptions = List(6) {
                ConsumptionUiModel(Random.nextInt(10000, 100000), Random.nextInt(10000, 100000))
            },
        )
    }
}
