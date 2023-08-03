package com.app.edonymyeon.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Posts(
    @SerialName("content")
    val post: List<Post>,
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
    @SerialName("totalPages")
    val totalPages: Int? = 0,
    @SerialName("totalElements")
    val totalElements: Int? = 0,
    @SerialName("numberOfElements")
    val numberOfElements: Int = 0,
    @SerialName("empty")
    val empty: Boolean? = null,
)

@Serializable // 안쓰는 속성들
data class Pageable(
    val sort: Sort,
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val unpaged: Boolean,
)

@Serializable
data class Sort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean,
)
