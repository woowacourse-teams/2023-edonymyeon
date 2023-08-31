package com.app.edonymyeon.presentation.uimodel

data class MyPostUiModel(
    val id: Long,
    val title: String,
    val image: String?,
    val content: String,
    val createdAt: DateUiModel,
    val consumption: ConsumptionUiModel,
)
