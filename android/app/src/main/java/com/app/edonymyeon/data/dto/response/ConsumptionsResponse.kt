package com.app.edonymyeon.data.dto.response

import com.app.edonymyeon.data.dto.ConsumptionAmountDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsumptionsResponse(
    @SerialName("startMonth") val startMonth: String,
    @SerialName("endMonth") val endMonth: String,
    @SerialName("consumptions") val consumptionAmounts: List<ConsumptionAmountDataModel>,
)
