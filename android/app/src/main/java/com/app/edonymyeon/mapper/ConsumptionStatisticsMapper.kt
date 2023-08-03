package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.response.ConsumptionsResponse
import com.app.edonymyeon.presentation.uimodel.ConsumptionStatisticsUiModel
import com.domain.edonymyeon.model.ConsumptionStatistics
import com.domain.edonymyeon.model.MonthRange
import java.time.YearMonth

fun ConsumptionStatisticsUiModel.toDomain(): ConsumptionStatistics {
    return ConsumptionStatistics(
        monthRange = MonthRange(startMonth, endMonth),
        consumptionAmounts = consumptionAmounts.map { it.toDomain() },
    )
}

fun ConsumptionsResponse.toDomain(): ConsumptionStatistics {
    val startMonth: YearMonth = YearMonth.parse(startMonth)
    val endMonth: YearMonth = YearMonth.parse(endMonth)
    return ConsumptionStatistics(
        monthRange = MonthRange(startMonth, endMonth),
        consumptionAmounts = consumptionAmounts.map { it.toDomain() },
    )
}

fun ConsumptionStatistics.toUiModel(): ConsumptionStatisticsUiModel {
    return ConsumptionStatisticsUiModel(
        startMonth = monthRange.start,
        endMonth = monthRange.end,
        consumptionAmounts = consumptionAmounts.map { it.toUiModel() },
    )
}
