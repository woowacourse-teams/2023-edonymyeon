package com.app.edonymyeon.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginDataModel(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)
