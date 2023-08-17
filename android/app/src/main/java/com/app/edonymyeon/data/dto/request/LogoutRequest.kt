package com.app.edonymyeon.data.dto.request

import com.app.edonymyeon.data.service.fcm.FCMToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequest(
    @SerialName("deviceToken") val deviceToken: String = FCMToken.getFCMToken() ?: "",
)
