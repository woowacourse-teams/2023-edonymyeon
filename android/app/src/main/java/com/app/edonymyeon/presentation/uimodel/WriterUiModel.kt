package com.app.edonymyeon.presentation.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WriterUiModel(
    val id: Long,
    private val _nickname: String,
    val profileImage: String?,
) : Parcelable {
    val nickname: String
        get() {
            if (_nickname.startsWith(KAKAO_PREFIX)) {
                return _nickname.substring(NICKNAME_START_INDEX, NICKNAME_END_INDEX)
            }
            return _nickname
        }

    companion object {
        private const val KAKAO_PREFIX = "#KAKAO"
        private const val NICKNAME_START_INDEX = 1
        private const val NICKNAME_END_INDEX = 11
    }
}
