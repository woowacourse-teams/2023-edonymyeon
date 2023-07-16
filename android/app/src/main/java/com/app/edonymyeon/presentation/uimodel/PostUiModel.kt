package com.app.edonymyeon.presentation.uimodel

import java.time.LocalDateTime

data class PostUiModel(
    val id: Long,
    val title: String,
    val price: Int,
    val content: String,
    val images: List<String>,
    val createdAt: LocalDateTime,
    val writer: WriterUiModel,
    val reactionCount: ReactionCountUiModel,
    val recommendation: RecommendationUiModel,
    val isScrap: Boolean,
    val isWriter: Boolean,
)
