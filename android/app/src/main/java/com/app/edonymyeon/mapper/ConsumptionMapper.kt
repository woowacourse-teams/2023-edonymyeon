package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.ConsumptionDataModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionUiModel
import com.domain.edonymyeon.model.Consumption

fun ConsumptionDataModel.toUiModel(): ConsumptionUiModel {
    return ConsumptionUiModel(
        type = type,
        purchasePrice = purchasePrice,
        year = year,
        month = month,
    )
}

fun ConsumptionDataModel.toDomain(): Consumption {
    return Consumption(
        type = type,
        purchasePrice = purchasePrice,
        year = year,
        month = month,
    )
}

fun Consumption.toUiModel(): ConsumptionUiModel {
    return ConsumptionUiModel(
        type = type,
        purchasePrice = purchasePrice,
        year = year,
        month = month,
    )
}
