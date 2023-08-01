package com.app.edonymyeon.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyPostsResponse(
    @SerialName("posts")
    val posts: List<MyPostResponse>,
)
