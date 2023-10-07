package com.app.edonymyeon.presentation.uimodel

import android.os.Parcelable
import com.domain.edonymyeon.model.Date
import kotlinx.parcelize.Parcelize
import java.time.format.DateTimeFormatter

@Parcelize
data class CommentDateUiModel(
    val createdAt: String,
) : Parcelable {
    override fun toString(): String {
        val localDateTime = Date(createdAt).localDateTime
        return localDateTime.format(DateTimeFormatter.ofPattern("MM.dd HH:mm"))
    }
}
