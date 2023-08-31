package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.response.NotificationsResponse
import com.domain.edonymyeon.model.Notifications

fun NotificationsResponse.toDomain(): Notifications {
    return Notifications(
        notifications = notification.map { it.toDomain() },
        isLast = isLast,
    )
}
