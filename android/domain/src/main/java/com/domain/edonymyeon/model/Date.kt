package com.domain.edonymyeon.model

import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class Date(
    val dateTime: String,
) {
    val yearMonth: YearMonth
        get() {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val dateTime = LocalDateTime.parse(dateTime, formatter)
            return YearMonth.of(dateTime.year, dateTime.month)
        }
}
