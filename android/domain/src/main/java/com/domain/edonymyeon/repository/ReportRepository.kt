package com.domain.edonymyeon.repository

interface ReportRepository {
    suspend fun postReport(
        type: String,
        postId: Long,
        repostId: Int,
        content: String?,
    ): Result<Unit>
}
