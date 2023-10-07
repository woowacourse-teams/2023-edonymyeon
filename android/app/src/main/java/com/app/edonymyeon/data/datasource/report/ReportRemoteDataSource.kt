package com.app.edonymyeon.data.datasource.report

import com.app.edonymyeon.data.dto.request.ReportRequest
import com.app.edonymyeon.data.service.ReportService
import retrofit2.Response
import javax.inject.Inject

class ReportRemoteDataSource @Inject constructor(
    private val reportService: ReportService,
) : ReportDataSource {

    override suspend fun postReport(reportRequest: ReportRequest): Response<Unit> {
        return reportService.postReport(reportRequest)
    }
}
