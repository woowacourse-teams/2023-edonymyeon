package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ReportRequest(
    val postId: Long,
    val reportId: Int,
    val content: String?,
)
