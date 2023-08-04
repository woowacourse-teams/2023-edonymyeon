package com.domain.edonymyeon.model

@JvmInline
value class Nickname private constructor(val value: String) {
    init {
        require(validate(value)) { ERROR_NEGATIVE_NICKNAME }
    }

    companion object {
        fun validate(value: String): Boolean = value.length > MINIMUM_NICKNAME_SIZE

        fun create(value: String): Nickname {
            return Nickname(value.trim())
        }

        private const val MINIMUM_NICKNAME_SIZE = 0
        private const val ERROR_NEGATIVE_NICKNAME = "닉네임 형식이 맞지 않습니다."
    }
}
