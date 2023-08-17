package com.domain.edonymyeon.model

data class UserRegistration(
    val email: Email,
    val password: Password,
    val nickname: Nickname,
    val deviceToken: String = "",
)
