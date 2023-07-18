package com.domain.edonymyeon.model

@JvmInline
value class Count(val value: Int) {
    init {
        require(value >= 0) { ERROR_NEGATIVE_NUMBER }
    }

    fun increase(): Count {
        return Count(value + 1)
    }

    fun decrease(): Count {
        val new = value - 1
        if (new < 0) {
            return Count(0)
        }
        return Count(value - 1)
    }

    operator fun plus(other: Count): Count {
        return Count(value + other.value)
    }

    companion object {
        private const val ERROR_NEGATIVE_NUMBER = "카운트는 음수가 될 수 없습니다."
    }
}
