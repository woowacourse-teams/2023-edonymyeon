package com.app.edonymyeon.data.datasource.report

import com.app.edonymyeon.data.dto.request.ReportRequest
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse

interface ReportDataSource {
    suspend fun postReport(
        reportRequest: ReportRequest,
    ): ApiResponse<Unit>
}
