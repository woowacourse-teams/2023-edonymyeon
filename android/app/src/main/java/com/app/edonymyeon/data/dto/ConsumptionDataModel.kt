package com.app.edonymyeon.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConsumptionDataModel(
    val type: String,
    val purchasePrice: Int,
    val year: Int,
    val month: Int,
)
