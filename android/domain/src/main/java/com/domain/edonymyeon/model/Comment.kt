package com.domain.edonymyeon.model

data class Comment(
    val id: Long,
    val image: String?,
    val comment: String,
    val isWriter: Boolean,
    val createdAt: String,
    val nickname: String,
)
