package com.domain.edonymyeon.model

import java.time.YearMonth

data class Consumptions(
    val startMonth: YearMonth,
    val endMonth: YearMonth,
    val consumptions: List<Consumption>,
) {
    val yearMonthList: List<String>
        get() {
            val list = mutableListOf<String>()

            var start = startMonth
            while (start.isBefore(endMonth)) {
                list.add(start.toString())
                start = start.plusMonths(1)
            }
            list.add(start.toString())

            return list
        }
}
