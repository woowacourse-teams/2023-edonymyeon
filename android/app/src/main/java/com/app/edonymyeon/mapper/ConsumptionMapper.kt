package com.app.edonymyeon.mapper

import com.app.edonymyeon.presentation.uimodel.ConsumptionUiModel
import com.domain.edonymyeon.model.Consumption

fun ConsumptionUiModel.toDomain(): Consumption {
    return Consumption(
        saving = saving,
        purchase = purchase,
    )
}
