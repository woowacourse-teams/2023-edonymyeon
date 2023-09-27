package com.app.edonymyeon.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationPreferenceDataModel(
    @SerialName("preferenceType")
    val preferenceType: String,
    @SerialName("enabled")
    val enabled: Boolean,
)
