package com.app.edonymyeon.presentation.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WriterUiModel(
    val id: Long,
    val nickname: NicknameUiModel,
    val profileImage: String?,
) : Parcelable
