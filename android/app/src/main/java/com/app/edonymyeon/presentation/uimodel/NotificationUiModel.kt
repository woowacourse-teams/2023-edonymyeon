package com.app.edonymyeon.presentation.uimodel

data class NotificationUiModel(
    val id: Long,
    val title: String,
    val navigateTo: String,
    val postId: Long,
    val read: Boolean,
)
