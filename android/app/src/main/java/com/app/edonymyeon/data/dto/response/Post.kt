package com.app.edonymyeon.data.dto.response

import com.app.edonymyeon.data.dto.ReactionCountDataModel
import com.app.edonymyeon.data.dto.WriterNickNameDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Long,
    val title: String,
    val image: String?,
    val content: String,
    @SerialName("writer") val writer: WriterNickNameDataModel,
    val createdAt: String,
    @SerialName("reactionCount") val reactionCount: ReactionCountDataModel,
)
