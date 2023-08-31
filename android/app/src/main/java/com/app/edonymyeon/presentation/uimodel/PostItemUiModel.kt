package com.app.edonymyeon.presentation.uimodel

data class PostItemUiModel(
    val id: Long,
    val title: String,
    val content: String,
    val thumbnailUrl: String?,
    val createdAt: DateUiModel,
    val nickname: NicknameUiModel,
    val reactionCount: ReactionCountUiModel,
)
