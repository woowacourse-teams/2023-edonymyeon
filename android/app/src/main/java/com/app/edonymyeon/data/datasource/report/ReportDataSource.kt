package com.app.edonymyeon.data.datasource.report

import com.app.edonymyeon.data.common.ApiResponse
import com.app.edonymyeon.data.dto.request.ReportRequest

interface ReportDataSource {
    suspend fun postReport(
        reportRequest: ReportRequest,
    ): ApiResponse<Unit>
}
