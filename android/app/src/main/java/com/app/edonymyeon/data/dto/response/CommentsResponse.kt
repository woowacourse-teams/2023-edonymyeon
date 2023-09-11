package com.app.edonymyeon.data.dto.response

import com.app.edonymyeon.data.dto.CommentDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentsResponse(
    @SerialName("commentCount")
    val commentCount: Int,
    @SerialName("comments")
    val comments: List<CommentDataModel>,
)
