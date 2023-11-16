package com.domain.edonymyeon.model

@JvmInline
value class Email private constructor(val value: String) {
    init {
        require(validate(value)) { ERROR_NEGATIVE_EMAIL }
    }
    companion object {
        fun validate(value: String): Boolean = Regex(Email_REGEX).matches(value)

        fun create(value: String): Email {
            return Email(value.trim())
        }

        private const val Email_REGEX =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        private const val ERROR_NEGATIVE_EMAIL = "이메일 형식이 맞지 않습니다."
    }
}
