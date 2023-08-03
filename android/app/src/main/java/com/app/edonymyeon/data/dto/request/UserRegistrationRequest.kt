package com.app.edonymyeon.data.dto.request

data class UserRegistrationRequest(
    val email: String,
    val password: String,
    val nickname: String,
)
