package com.domain.edonymyeon.model

data class Recommendation(
    val upCount: Count,
    val downCount: Count,
    val isUp: Boolean,
    val isDown: Boolean,
) {
    val progress: Int
        get() {
            val total = (upCount + downCount).value
            if (total == 0) {
                return 50
            }
            return (upCount.value.toFloat() / total.toFloat() * 100).toInt()
        }
}
