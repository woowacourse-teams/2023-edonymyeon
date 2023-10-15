package com.app.edonymyeon.data.dto.response

import com.app.edonymyeon.data.dto.NotificationPreferenceDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationPreferenceResponse(
    @SerialName("notifications")
    val notifications: List<NotificationPreferenceDataModel>,
)
