package com.app.edonymyeon.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Posts(
    @SerialName("content")
    val post: List<Post>,
    @SerialName("last")
    val isLast: Boolean,
)
