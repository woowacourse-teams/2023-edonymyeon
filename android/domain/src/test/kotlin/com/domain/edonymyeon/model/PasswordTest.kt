package com.domain.edonymyeon.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class PasswordTest {
    @Test
    fun `패스워드에 공백을 넣으면 제거된다`() {
        val password = Password.create("1111AAAA! ")
        assertThat(password.value).isEqualTo("1111AAAA!")
    }

    @Test
    fun `패스워드는 8글자 이상이고 알파벳 1개, 숫자 1개, 특수문자 1개를 각각 최소한 포함해야 한다`() {
        assertDoesNotThrow {
            Password.create("11234a2!")
        }
    }

    @Test
    fun `패스워드가 8글자 이상이고 알파벳을 1개이상 포함하지 않으면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            Password.create("!1231211")
        }
    }

    @Test
    fun `패스워드가 8글자 이상이고 숫자를 1개이상 포함하지 않으면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            Password.create("AAAAA!!!!")
        }
    }

    @Test
    fun `패스워드가 8글자 이상이고 특수문자를 1개이상 포함하지 않으면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            Password.create("AAAA1111")
        }
    }

    @Test
    fun `패스워드가 8글자 미만이면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            Password.create("A!A1111")
        }
    }

    @Test
    fun `패스워드가 30글자 초과이면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            Password.create("A!A111A!A111A!A111A!A111A!A1111")
        }
    }

    @Test
    fun `패스워드 검증에서 8글자 이상이고 알파벳 1개, 숫자 1개, 특수문자 1개를 각각 최소한 포함하면 true를 반환한다`() {
        val result = Password.validate("11234a2!")
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun `패스워드 검증에서 8글자 이상이고 알파벳을 1개이상 포함하지 않으면 false를 반환한다`() {
        val result = Password.validate("!1231211")
        assertThat(result).isEqualTo(false)
    }

    @Test
    fun `패스워드 검증에서 8글자 이상이고 숫자를 1개이상 포함하지 않으면 false를 반환한다`() {
        val result = Password.validate("AAAAA!!!!")
        assertThat(result).isEqualTo(false)
    }

    @Test
    fun `패스워드 검증에서 8글자 이상이고 특수문자를 1개이상 포함하지 않으면 false를 반환한다`() {
        val result = Password.validate("AAAA1111")
        assertThat(result).isEqualTo(false)
    }

    @Test
    fun `패스워드 검증에서 8글자 미만이면 false를 반환한다`() {
        val result = Password.validate("A!A1111")
        assertThat(result).isEqualTo(false)
    }

    @Test
    fun `패스워드 검증에서 30글자 초과이면 false를 반환한다`() {
        val result = Password.validate("A!A111A!A111A!A111A!A111A!A1111")
        assertThat(result).isEqualTo(false)
    }

    @Test
    fun `동일한 패스워드이다`() {
        val password = Password.create("1111AAA!")
        val result = password.isEqualTo("1111AAA!")
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun `다른 패스워드이다`() {
        val password = Password.create("1111AAA!")
        val result = password.isEqualTo("1111AAAA!")
        assertThat(result).isEqualTo(false)
    }
}
