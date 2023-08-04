package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationRequest(
    val email: String,
    val password: String,
    val nickname: String,
)
