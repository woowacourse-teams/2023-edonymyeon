package com.app.edonymyeon.presentation.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommentUiModel(
    val id: Long,
    val image: String?,
    val comment: String,
    val isWriter: Boolean,
    val createdAt: CommentDateUiModel,
    val nicknameUiModel: NicknameUiModel,
) : Parcelable
