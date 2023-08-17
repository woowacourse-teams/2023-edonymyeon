package com.domain.edonymyeon.model

data class Notification(
    val id: Long,
    val title: String,
    val navigateTo: String,
    val postId: Long,
    val read: Boolean,
)
