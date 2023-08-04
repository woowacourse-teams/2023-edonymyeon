package com.app.edonymyeon.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyPostsResponse(
    @SerialName("content")
    val posts: List<MyPostResponse>,
    @SerialName("pageable")
    val pageable: Pageable,
    @SerialName("size")
    val size: Int = 0,
    @SerialName("number")
    val number: Int = 0,
    @SerialName("sort")
    val sort: Sort,
    @SerialName("first")
    val first: Boolean? = null,
    @SerialName("last")
    val isLast: Boolean,
    @SerialName("numberOfElements")
    val numberOfElements: Int = 0,
    @SerialName("empty")
    val empty: Boolean? = null,
)
