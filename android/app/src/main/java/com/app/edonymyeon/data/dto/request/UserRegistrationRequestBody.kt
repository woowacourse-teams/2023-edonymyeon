package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationRequestBody(
    val email: String,
    val password: String,
    val nickname: String,
)
