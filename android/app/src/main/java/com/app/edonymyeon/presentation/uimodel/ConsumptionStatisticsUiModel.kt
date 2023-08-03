package com.app.edonymyeon.presentation.uimodel

import java.time.YearMonth

data class ConsumptionStatisticsUiModel(
    val startMonth: YearMonth,
    val endMonth: YearMonth,
    val consumptionAmounts: List<ConsumptionAmountUiModel>,
)
