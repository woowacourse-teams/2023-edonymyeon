package com.app.edonymyeon.data.datasource.report

import com.app.edonymyeon.data.common.ApiResponse
import com.app.edonymyeon.data.dto.request.ReportRequest
import com.app.edonymyeon.data.service.ReportService
import javax.inject.Inject

class ReportRemoteDataSource @Inject constructor(
    private val reportService: ReportService,
) : ReportDataSource {

    override suspend fun postReport(reportRequest: ReportRequest): ApiResponse<Unit> {
        return reportService.postReport(reportRequest)
    }
}
