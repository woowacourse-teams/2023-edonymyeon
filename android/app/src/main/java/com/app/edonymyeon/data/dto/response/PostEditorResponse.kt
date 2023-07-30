package com.app.edonymyeon.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostEditorResponse(
    @SerialName("id")
    val id: Long,
)
