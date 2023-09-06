package com.app.edonymyeon.presentation.uimodel

import com.domain.edonymyeon.model.Date
import java.time.format.DateTimeFormatter

data class CommentDateUiModel(
    val createdAt: String,
) {
    override fun toString(): String {
        val localDateTime = Date(createdAt).localDateTime
        return localDateTime.format(DateTimeFormatter.ofPattern("MM.dd HH:mm"))
    }
}
