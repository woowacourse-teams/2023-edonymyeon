package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.ConsumptionAmountDataModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionAmountUiModel
import com.domain.edonymyeon.model.ConsumptionAmount

fun ConsumptionAmountUiModel.toDomain(): ConsumptionAmount {
    return ConsumptionAmount(
        saving = saving,
        purchase = purchase,
    )
}

fun ConsumptionAmountDataModel.toDomain(): ConsumptionAmount {
    return ConsumptionAmount(
        saving = savingPrice,
        purchase = purchasePrice,
    )
}

fun ConsumptionAmount.toUiModel(): ConsumptionAmountUiModel {
    return ConsumptionAmountUiModel(
        saving = saving,
        purchase = purchase,
    )
}
