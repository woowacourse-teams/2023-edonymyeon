package com.domain.edonymyeon.model

import org.junit.jupiter.api.Test
import java.time.YearMonth

class DateTest {

    @Test
    fun `"2023-08-01"의 year 2023, month 8이다`() {
        val date = Date(
            "2023-08-01T22:11:53.111371",
        )

        val actual = date.yearMonth
        val expected = YearMonth.of(2023, 8)

        assert(actual == expected)
    }
}
