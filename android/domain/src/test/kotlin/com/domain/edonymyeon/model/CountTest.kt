package com.domain.edonymyeon.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class CountTest {
    @Test
    fun `카운트는 음수가 될 수 없다`() {
        assertThrows<IllegalArgumentException> {
            Count(-1)
        }
    }

    @Test
    fun `카운트가 1 증가한다`() {
        var count = Count(5)
        count = count.increase()
        assertEquals(6, count.value)
    }

    @Test
    fun `카운트가 1 감소한다`() {
        var count = Count(5)
        count = count.decrease()
        assertEquals(4, count.value)
    }

    @Test
    fun `카운트가 0이면 감소하지 않는다`() {
        var count = Count(0)
        count = count.decrease()
        assertEquals(1, count.value)
    }
}
