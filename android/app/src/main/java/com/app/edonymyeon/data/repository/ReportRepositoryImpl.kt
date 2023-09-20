package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.report.ReportDataSource
import com.app.edonymyeon.data.dto.request.ReportRequest
import com.domain.edonymyeon.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(private val reportDataSource: ReportDataSource) : ReportRepository {
    override suspend fun postReport(
        type: String,
        postId: Long,
        repostId: Int,
        content: String?,
    ): Result<Unit> {
        val result = reportDataSource.postReport(
            ReportRequest(
                type,
                postId,
                repostId,
                content,
            ),
        )

        return if (result.isSuccessful) {
            Result.success(result.body() ?: Unit)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
