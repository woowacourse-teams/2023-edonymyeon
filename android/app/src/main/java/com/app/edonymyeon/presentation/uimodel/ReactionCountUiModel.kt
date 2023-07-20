package com.app.edonymyeon.presentation.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReactionCountUiModel(
    val viewCount: Int,
    val commentCount: Int,
    val scrapCount: Int,
) : Parcelable
