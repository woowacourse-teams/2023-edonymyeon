package com.app.edonymyeon.data.dto.response

import com.app.edonymyeon.data.dto.ReactionCountDataModel
import com.app.edonymyeon.data.dto.WriterNickNameDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("image") val image: String?,
    @SerialName("content") val content: String,
    @SerialName("writer") val writer: WriterNickNameDataModel,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("reactionCount") val reactionCount: ReactionCountDataModel,
)
