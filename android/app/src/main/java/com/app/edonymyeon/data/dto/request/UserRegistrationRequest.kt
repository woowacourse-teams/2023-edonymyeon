package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationRequest(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("deviceToken")
    val deviceToken: String?,
)
