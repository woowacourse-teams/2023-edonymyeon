package com.app.edonymyeon.data.datasource.report

import com.app.edonymyeon.data.dto.request.ReportRequest
import retrofit2.Response

interface ReportDataSource {
    suspend fun postReport(
        reportRequest: ReportRequest,
    ): Response<Unit>
}
