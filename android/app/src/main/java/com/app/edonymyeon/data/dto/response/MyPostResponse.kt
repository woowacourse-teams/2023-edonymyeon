package com.app.edonymyeon.data.dto.response

import com.app.edonymyeon.data.dto.ConsumptionDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyPostResponse(
    @SerialName("email") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("image") val image: String? = null,
    @SerialName("content") val content: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("consumption") val consumption: ConsumptionDataModel,
)
