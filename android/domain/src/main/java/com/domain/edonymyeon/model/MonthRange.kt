package com.domain.edonymyeon.model

import java.time.YearMonth

data class MonthRange(
    val start: YearMonth,
    val end: YearMonth,
) {
    val yearMonthList: List<String>
        get() {
            val list = mutableListOf<String>()

            var temp = start
            while (temp.isBefore(end)) {
                list.add(temp.toString())
                temp = temp.plusMonths(1)
            }
            list.add(temp.toString())

            return list
        }
}
