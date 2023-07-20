package com.app.edonymyeon.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WriterDataModel(
    @SerialName("id") val writerId: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("profileImage") val profileImage: String?,
)
