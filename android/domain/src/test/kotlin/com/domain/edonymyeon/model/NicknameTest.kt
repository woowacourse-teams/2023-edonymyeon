package com.domain.edonymyeon.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class NicknameTest {
    @Test
    fun `닉네임은 0글자 이상이다`() {
        assertDoesNotThrow {
            Nickname.create("가")
        }
    }

    @Test
    fun `닉네임이 0글자이면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            Nickname.create("")
        }
    }

    @Test
    fun `닉네임에 공백을 넣으면 제거된다`() {
        val nickname = Nickname.create("가 ")
        assertThat(nickname.value).isEqualTo("가")
    }

    @Test
    fun `닉네임 검증에서 0글자가 넘으면 true가 반환된다`() {
        val result = Nickname.validate("가")
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun `닉네임 검증에서 0글자가 넘지 않으면 false가 반환된다`() {
        val result = Nickname.validate("")
        assertThat(result).isEqualTo(false)
    }
}
