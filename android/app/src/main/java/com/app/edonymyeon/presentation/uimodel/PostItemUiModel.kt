package com.app.edonymyeon.presentation.uimodel

import java.time.LocalDateTime

data class PostItemUiModel(
    val id: Long,
    val title: String,
    val content: String,
    val thumbnailUrl: String,
    val createdAt: LocalDateTime,
    val writer: WriterUiModel,
    val reactionCount: ReactionCountUiModel,
)
