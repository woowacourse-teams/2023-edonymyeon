package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PostEditorRequest(
    val title: String,
    val content: String,
    val price: Int,
    val images: List<PartWrapper>,
)
