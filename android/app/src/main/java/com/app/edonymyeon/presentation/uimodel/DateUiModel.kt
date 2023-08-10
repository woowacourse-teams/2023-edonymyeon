package com.app.edonymyeon.presentation.uimodel

import android.os.Parcelable
import com.domain.edonymyeon.model.Date
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateUiModel(
    val createdAt: String,
) : Parcelable {
    override fun toString(): String {
        val time = Date(createdAt).intervalDateTime
        return getTimeAgo(time)
    }

    private fun getTimeAgo(diffTime: Long): String {
        val sec = diffTime / TIME_MAXIMUM_SEC
        val min = sec / TIME_MAXIMUM_MIN
        val hour = min / TIME_MAXIMUM_HOUR
        val day = hour / TIME_MAXIMUM_DAY
        val month = day / TIME_MAXIMUM_MONTH

        return when {
            diffTime < TIME_MAXIMUM_SEC -> "방금 전"
            sec < TIME_MAXIMUM_MIN -> "${sec}분 전"
            min < TIME_MAXIMUM_HOUR -> "${min}시간 전"
            hour < TIME_MAXIMUM_DAY -> "${hour}일 전"
            day < TIME_MAXIMUM_MONTH -> "${day}달 전"
            else -> "${month}년 전"
        }
    }

    companion object {
        private const val TIME_MAXIMUM_SEC = 60
        private const val TIME_MAXIMUM_MIN = 60
        private const val TIME_MAXIMUM_HOUR = 24
        private const val TIME_MAXIMUM_DAY = 30
        private const val TIME_MAXIMUM_MONTH = 12
    }
}
