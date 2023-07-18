package com.domain.edonymyeon.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RecommendationTest {
    @Test
    fun `추천, 비추천 수가 0이면 기본 프로그레스 비율값은 50이다`() {
        val recommendation = Recommendation(
            upCount = Count(0),
            downCount = Count(0),
            isUp = false,
            isDown = false,
        )

        assertEquals(50, recommendation.progress)
    }

    @Test
    fun `추천 수가 1, 비추천 수가 0이면 프로그레스 비율값은 100이다`() {
        val recommendation = Recommendation(
            upCount = Count(1),
            downCount = Count(0),
            isUp = false,
            isDown = false,
        )

        assertEquals(100, recommendation.progress)
    }

    @Test
    fun `추천 수가 0, 비추천 수가 1이면 프로그레스 비율값은 0이다`() {
        val recommendation = Recommendation(
            upCount = Count(0),
            downCount = Count(1),
            isUp = false,
            isDown = false,
        )

        assertEquals(0, recommendation.progress)
    }

    @Test
    fun `추천 수가 1, 비추천 수가 2이면 프로그레스 비율값은 33이다`() {
        val recommendation = Recommendation(
            upCount = Count(1),
            downCount = Count(2),
            isUp = false,
            isDown = false,
        )

        assertEquals(33, recommendation.progress)
    }
}
