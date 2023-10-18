package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileUpdateRequest(
    @SerialName("nickname") val nickname: String,
    @SerialName("isImageChanged") val isImageChanged: Boolean,
)
