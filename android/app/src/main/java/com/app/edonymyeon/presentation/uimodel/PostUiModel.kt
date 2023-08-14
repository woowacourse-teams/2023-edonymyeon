package com.app.edonymyeon.presentation.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostUiModel(
    val id: Long,
    val title: String,
    val price: Int,
    val content: String,
    val images: List<String>,
    val createdAt: DateUiModel,
    val writer: WriterUiModel,
    val reactionCount: ReactionCountUiModel,
    val recommendation: RecommendationUiModel,
    val isScrap: Boolean,
    val isWriter: Boolean,
) : Parcelable
