package com.domain.edonymyeon.repository

interface ReportRepository {
    suspend fun postReport(
        postId: Long,
        repostId: Int,
        content: String?,
    ): Result<Unit>
}
