package com.app.edonymyeon.presentation.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecommendationUiModel(
    val upCount: Int,
    val downCount: Int,
    val isUp: Boolean,
    val isDown: Boolean,
    val progress: Int,
) : Parcelable
