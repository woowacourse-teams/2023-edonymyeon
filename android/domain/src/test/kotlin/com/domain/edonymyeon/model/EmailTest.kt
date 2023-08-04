package com.domain.edonymyeon.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class EmailTest {
    @Test
    fun `이메일 형식으로 생성한다`() {
        assertDoesNotThrow {
            Email.create("abc@naver.com")
        }
    }

    @Test
    fun `이메일 형식이 아니면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            Email.create("abc@")
        }
    }

    @Test
    fun `이메일에 공백을 넣으면 제거된다`() {
        val email = Email.create("abc@naver.com ")
        assertThat(email.value).isEqualTo("abc@naver.com")
    }

    @Test
    fun `이메일 검증에서 이메일 형식이면 true가 반환된다`() {
        val result = Email.validate("abc@naver.com")
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun `이메일 검증에서 이메일 형식이 아니면 false가 반환된다`() {
        val result = Email.validate("abc")
        assertThat(result).isEqualTo(false)
    }
}
