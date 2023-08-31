package com.app.edonymyeon.data.dto.response

import com.app.edonymyeon.data.dto.NotificationDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationsResponse(
    @SerialName("content")
    val notification: List<NotificationDataModel>,
    @SerialName("last")
    val isLast: Boolean,
)
