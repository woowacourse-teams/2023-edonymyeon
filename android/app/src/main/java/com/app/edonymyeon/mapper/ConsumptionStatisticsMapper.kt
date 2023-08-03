package com.app.edonymyeon.mapper

import com.app.edonymyeon.presentation.uimodel.ConsumptionStatisticsUiModel
import com.domain.edonymyeon.model.ConsumptionStatistics
import com.domain.edonymyeon.model.MonthRange

fun ConsumptionStatisticsUiModel.toDomain(): ConsumptionStatistics {
    return ConsumptionStatistics(
        monthRange = MonthRange(startMonth, endMonth),
        consumptionAmounts = consumptionAmounts.map { it.toDomain() },
    )
}
