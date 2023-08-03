package com.domain.edonymyeon.model

data class ConsumptionStatistics(
    val monthRange: MonthRange,
    val consumptionAmounts: List<ConsumptionAmount>,
)
