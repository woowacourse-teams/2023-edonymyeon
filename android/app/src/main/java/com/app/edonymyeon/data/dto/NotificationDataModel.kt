package com.app.edonymyeon.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDataModel(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("navigateTo")
    val navigateTo: String,
    @SerialName("postId")
    val postId: Long?,
    @SerialName("read")
    val read: Boolean,
)
