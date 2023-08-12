package com.domain.edonymyeon.model

import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class Date(
    val dateTime: String,
) {
    val yearMonth: YearMonth
        get() {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val dateTime = LocalDateTime.parse(dateTime, formatter)
            return YearMonth.of(dateTime.year, dateTime.month)
        }
    private val intervalDateTime: Long
        get() {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val dateTime = LocalDateTime.parse(dateTime, formatter)
            return ChronoUnit.SECONDS.between(dateTime, LocalDateTime.now())
        }

    fun getTimeAgo(diffTime: Long = intervalDateTime): Pair<Time, Long> {
        val min = diffTime / TIME_MAXIMUM_SEC
        val hour = min / TIME_MAXIMUM_MIN
        val day = hour / TIME_MAXIMUM_HOUR
        val month = day / TIME_MAXIMUM_DAY
        val year = month / TIME_MAXIMUM_MONTH

        return when {
            diffTime < TIME_MAXIMUM_SEC -> Pair(Time.SEC, diffTime)
            min < TIME_MAXIMUM_MIN -> Pair(Time.MIN, min)
            hour < TIME_MAXIMUM_HOUR -> Pair(Time.HOUR, hour)
            day < TIME_MAXIMUM_DAY -> Pair(Time.DAY, day)
            month < TIME_MAXIMUM_MONTH -> Pair(Time.MONTH, month)
            else -> Pair(Time.YEAR, year)
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
