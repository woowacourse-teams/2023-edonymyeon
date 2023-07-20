package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody

@Serializable
data class PartWrapper(
    @Contextual
    @SerialName("part")
    val part: @Contextual MultipartBody.Part,
)
