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

    @Test
    fun `diffTime이 30초인 경우 SEC, 30을 반환한다`() {
        val date = Date("")

        val actual = date.getTimeAgo(30)
        val expected = Pair(Time.SEC, 30.toLong())

        assert(actual == expected)
    }

    @Test
    fun `diffTime이 160초인 경우 MIN, 2를 반환한다`() {
        val date = Date("")

        val actual = date.getTimeAgo(160)
        val expected = Pair(Time.MIN, 2.toLong())

        assert(actual == expected)
    }

    @Test
    fun `diffTime이 3800초인 경우 HOUR, 1을 반환한다`() {
        val date = Date("")

        val actual = date.getTimeAgo(3800)
        val expected = Pair(Time.HOUR, 1.toLong())

        assert(actual == expected)
    }

    @Test
    fun `diffTime이 345600초인 경우 DAY, 4을 반환한다`() {
        val date = Date("")

        val actual = date.getTimeAgo(345600)
        val expected = Pair(Time.DAY, 4.toLong())

        assert(actual == expected)
    }

    @Test
    fun `diffTime이 31557600초인 경우 YEAR, 1을 반환한다`() {
        val date = Date("")

        val actual = date.getTimeAgo(31557600)
        val expected = Pair(Time.YEAR, 1.toLong())

        assert(actual == expected)
    }
}
