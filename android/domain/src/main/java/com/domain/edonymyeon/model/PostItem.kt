package com.domain.edonymyeon.model

data class PostItem(
    val id: Long,
    val title: String,
    val image: String?,
    val content: String,
    val createdAt: String,
    val nickname: String,
    val reactionCount: ReactionCount,
)
