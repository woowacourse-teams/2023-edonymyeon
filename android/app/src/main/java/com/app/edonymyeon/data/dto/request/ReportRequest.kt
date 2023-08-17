package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportRequest(
    @SerialName("postId") val postId: Long,
    @SerialName("reportId") val reportId: Int,
    @SerialName("content") val content: String?,
)
