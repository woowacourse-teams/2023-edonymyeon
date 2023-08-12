package com.app.edonymyeon.presentation.uimodel

import android.os.Parcelable
import com.domain.edonymyeon.model.Date
import com.domain.edonymyeon.model.Time
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateUiModel(
    val createdAt: String,
) : Parcelable {
    override fun toString(): String {
        val times = Date(createdAt).getTimeAgo()
        return when (times.first) {
            Time.SEC -> "방금 전"
            Time.MIN -> "${times.second}분 전"
            Time.HOUR -> "${times.second}시간 전"
            Time.DAY -> "${times.second}일 전"
            Time.MONTH -> "${times.second}달 전"
            Time.YEAR -> "${times.second}년 전"
        }
    }
}
