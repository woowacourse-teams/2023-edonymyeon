package com.app.edonymyeon.presentation.uimodel

data class AllPostItemUiModel(
    val id: Int,
    val title: String,
    val content: String,
    val nickname: String,
    val date: String,
    val thumbnailUrl: String?
)
