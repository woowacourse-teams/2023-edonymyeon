package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationPreferenceRequest(
    @SerialName("preferenceType")
    val preferenceType: String,
)
