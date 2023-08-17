package com.app.edonymyeon.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsumptionDataModel(
    @SerialName("type") val type: String,
    @SerialName("purchasePrice") val purchasePrice: Int,
    @SerialName("year") val year: Int,
    @SerialName("month") val month: Int,
)
