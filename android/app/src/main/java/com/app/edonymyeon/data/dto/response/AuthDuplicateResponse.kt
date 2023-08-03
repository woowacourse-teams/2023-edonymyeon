package com.app.edonymyeon.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthDuplicateResponse(
    val isUnique: Boolean,
)
