package com.app.edonymyeon.data.dto.response

import com.app.edonymyeon.data.dto.ConsumptionDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyPostResponse(
    val id: Long,
    val title: String,
    val image: String? = null,
    val content: String,
    val createdAt: String,
    @SerialName("consumption") val consumption: ConsumptionDataModel,
)
