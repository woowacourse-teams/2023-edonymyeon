package com.domain.edonymyeon.model

import org.junit.jupiter.api.Test
import java.time.YearMonth

internal class MonthRangeTest {
    @Test
    fun `2022년 11월부터 2023년 4월 사이의 월들을 구한다`() {
        val monthRange = MonthRange(
            YearMonth.of(2022, 11),
            YearMonth.of(2023, 4),
        )

        val actual = monthRange.yearMonthList
        val expected = listOf("2022-11", "2022-12", "2023-01", "2023-02", "2023-03", "2023-04")

        assert(actual == expected)
    }
}
