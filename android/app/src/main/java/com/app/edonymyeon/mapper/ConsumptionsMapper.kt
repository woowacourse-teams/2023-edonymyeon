package com.app.edonymyeon.mapper

import com.app.edonymyeon.presentation.uimodel.ConsumptionsUiModel
import com.domain.edonymyeon.model.Consumptions

fun ConsumptionsUiModel.toDomain(): Consumptions {
    return Consumptions(
        startMonth = startMonth,
        endMonth = endMonth,
        consumptions = consumptions.map { it.toDomain() },
    )
}
