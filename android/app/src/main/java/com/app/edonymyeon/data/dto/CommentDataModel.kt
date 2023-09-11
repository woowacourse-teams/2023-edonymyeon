package com.app.edonymyeon.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDataModel(
    @SerialName("id") val id: Long,
    @SerialName("image") val image: String?,
    @SerialName("comment") val comment: String,
    @SerialName("isWriter") val isWriter: Boolean,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("writer") val writerNickNameDataModel: WriterNickNameDataModel,
)
