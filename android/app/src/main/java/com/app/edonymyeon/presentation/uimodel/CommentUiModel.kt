package com.app.edonymyeon.presentation.uimodel

data class CommentUiModel(
    val id: Long,
    val image: String?,
    val comment: String,
    val isWriter: Boolean,
    val createdAt: CommentDateUiModel,
    val nicknameUiModel: NicknameUiModel,
)
