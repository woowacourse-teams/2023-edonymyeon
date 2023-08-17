package com.app.edonymyeon.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthDuplicateResponse(
    @SerialName("email") val isUnique: Boolean,
)
