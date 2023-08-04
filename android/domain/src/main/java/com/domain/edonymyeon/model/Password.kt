package com.domain.edonymyeon.model

@JvmInline
value class Password private constructor(val value: String) {
    init {
        require(validate(value)) { ERROR_NEGATIVE_PASSWORD }
    }

    fun isEqualTo(value: String): Boolean = this.value == value

    companion object {
        fun validate(value: String): Boolean = Regex(PASSWORD_REGEX).matches(value)

        fun create(value: String): Password {
            return Password(value.trim())
        }

        private const val PASSWORD_REGEX =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%#?&])[A-Za-z\\d@\$!%*#?&]{8,30}$"
        private const val ERROR_NEGATIVE_PASSWORD = "비밀번호 형식이 맞지 않습니다."
    }
}
