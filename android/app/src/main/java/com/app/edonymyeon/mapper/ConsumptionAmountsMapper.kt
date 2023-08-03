package com.app.edonymyeon.mapper

import com.app.edonymyeon.presentation.uimodel.ConsumptionAmountUiModel
import com.domain.edonymyeon.model.ConsumptionAmount

fun ConsumptionAmountUiModel.toDomain(): ConsumptionAmount {
    return ConsumptionAmount(
        saving = saving,
        purchase = purchase,
    )
}
