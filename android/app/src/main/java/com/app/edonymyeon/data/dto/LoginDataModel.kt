package com.app.edonymyeon.data.dto

import com.app.edonymyeon.data.service.fcm.FCMToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginDataModel(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("deviceToken") val deviceToken: String = FCMToken.getFCMToken() ?: "",
)
