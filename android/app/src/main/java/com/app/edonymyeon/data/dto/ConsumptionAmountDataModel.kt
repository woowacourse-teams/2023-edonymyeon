package com.app.edonymyeon.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ConsumptionAmountDataModel(
    @SerialName("purchasePrice") val purchasePrice: Int,
    @SerialName("savingPrice") val savingPrice: Int,
)
