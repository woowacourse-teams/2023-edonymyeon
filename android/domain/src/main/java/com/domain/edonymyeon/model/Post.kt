package com.domain.edonymyeon.model

import java.time.LocalDateTime

data class Post(
    val id: Long,
    val title: String,
    val price: Int,
    val content: String,
    val images: List<String>,
    val createdAt: LocalDateTime,
    val writer: Writer,
    val reactionCount: ReactionCount,
    val recommendation: Recommendation,
    val isScrap: Boolean,
    val isWriter: Boolean,
)
