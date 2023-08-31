package com.app.edonymyeon.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyPostsResponse(
    @SerialName("content")
    val posts: List<MyPostResponse>,
    @SerialName("last")
    val isLast: Boolean,
)
