package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.NotificationDataModel
import com.app.edonymyeon.presentation.uimodel.NotificationUiModel
import com.domain.edonymyeon.model.Notification

fun NotificationDataModel.toDomain(): Notification {
    return Notification(
        id = id,
        title = title,
        navigateTo = navigateTo,
        postId = postId,
        read = read,
    )
}

fun Notification.toUiModel(): NotificationUiModel {
    return NotificationUiModel(
        id = id,
        title = title,
        navigateTo = navigateTo,
        postId = postId,
        read = read,
    )
}
