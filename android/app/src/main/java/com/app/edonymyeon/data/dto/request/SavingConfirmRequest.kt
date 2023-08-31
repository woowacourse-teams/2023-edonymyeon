package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavingConfirmRequest(
    @SerialName("year") val year: Int,
    @SerialName("month") val month: Int,
)
