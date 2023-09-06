package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportRequest(
    @SerialName("type") val type: String,
    @SerialName("referenceId") val commentId: Long,
    @SerialName("reportCategoryId") val reportId: Int,
    @SerialName("content") val content: String?,
)
