package com.domain.edonymyeon.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PageTest {
    @Test
    fun `페이지를 증가시킨다`() {
        var page = Page(0)
        page = page.increasePage()
        assertThat(page.value).isEqualTo(1)
    }

    @Test
    fun `페이지는 0 미만이 될 수 없다`() {
        assertThrows<IllegalArgumentException> {
            var page = Page(-1)
        }
    }

    @Test
    fun `페이지를 초기화 한다`() {
        var page = Page(1)
        page = page.initPage()
        assertThat(page.value).isEqualTo(0)
    }
}
