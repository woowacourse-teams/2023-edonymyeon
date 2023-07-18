package com.app.edonymyeon.presentation.uimodel

data class RecommendationUiModel(
    val upCount: Int,
    val downCount: Int,
    val isUp: Boolean,
    val isDown: Boolean,
    val progress: Int,
)
