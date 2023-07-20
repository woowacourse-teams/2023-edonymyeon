package com.app.edonymyeon.data.dto.response

import com.app.edonymyeon.data.dto.ReactionCountDataModel
import com.app.edonymyeon.data.dto.WriterDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDetailResponse(
    val id: Long,
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("price") val price: Int,
    @SerialName("content") val content: String,
    @SerialName("images") val images: List<String>,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("writer") val writer: WriterDataModel,
    @SerialName("reactionCount") val reactionCount: ReactionCountDataModel,
    @SerialName("upCount") val upCount: Int,
    @SerialName("downCount") val downCount: Int,
    @SerialName("isUp") val isUp: Boolean,
    @SerialName("isDown") val isDown: Boolean,
    @SerialName("isScrap") val isScrap: Boolean,
    @SerialName("isWriter") val isWriter: Boolean,
)
