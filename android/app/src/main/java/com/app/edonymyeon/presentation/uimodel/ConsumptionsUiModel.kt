package com.app.edonymyeon.presentation.uimodel

import java.time.YearMonth

data class ConsumptionsUiModel(
    val startMonth: YearMonth,
    val endMonth: YearMonth,
    val consumptions: List<ConsumptionUiModel>,
)
