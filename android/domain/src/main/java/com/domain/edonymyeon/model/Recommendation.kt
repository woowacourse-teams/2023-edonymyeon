package com.domain.edonymyeon.model

data class Recommendation(
    val upCount: Int,
    val downCount: Int,
    val isUp: Boolean,
    val isDown: Boolean,
) {
    val progress: Int
        get() {
            val total = upCount + downCount
            if (total == 0) {
                return 0
            }
            return (upCount.toFloat() / total.toFloat() * 100).toInt()
        }
}
