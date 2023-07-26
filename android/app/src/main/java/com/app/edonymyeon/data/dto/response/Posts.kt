package com.app.edonymyeon.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Posts(
    @SerialName("posts")
    val post: List<Post>
)
