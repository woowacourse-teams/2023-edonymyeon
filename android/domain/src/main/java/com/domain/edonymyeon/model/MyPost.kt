package com.domain.edonymyeon.model

data class MyPost(
    val id: Long,
    val title: String,
    val image: String?,
    val content: String,
    val createdAt: String,
    val consumption: Consumption,
    val reactionCount: ReactionCount,
)
