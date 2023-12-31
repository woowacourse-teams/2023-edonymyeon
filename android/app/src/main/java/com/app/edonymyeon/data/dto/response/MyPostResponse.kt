package com.app.edonymyeon.data.dto.response

import com.app.edonymyeon.data.dto.ConsumptionDataModel
import com.app.edonymyeon.data.dto.ReactionCountDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyPostResponse(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("image") val image: String? = null,
    @SerialName("content") val content: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("consumption") val consumption: ConsumptionDataModel,
    @SerialName("reactionCount") val reactionCount: ReactionCountDataModel,
)
