package com.app.edonymyeon.presentation.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NicknameUiModel(
    val nickname: String,
) : Parcelable {
    override fun toString(): String {
        if (nickname.startsWith(KAKAO_PREFIX)) {
            return nickname.substring(NICKNAME_START_INDEX, NICKNAME_END_INDEX)
        }
        return nickname
    }

    companion object {
        private const val KAKAO_PREFIX = "#KAKAO"
        private const val NICKNAME_START_INDEX = 1
        private const val NICKNAME_END_INDEX = 11
    }
}
