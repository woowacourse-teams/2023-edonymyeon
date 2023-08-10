package com.app.edonymyeon.data.datasource.report

import com.app.edonymyeon.data.dto.request.ReportRequest
import com.app.edonymyeon.data.service.ReportService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response

class ReportRemoteDataSource : ReportDataSource {

    private val reportService: ReportService =
        RetrofitClient.getInstance().create(ReportService::class.java)

    override suspend fun postReport(reportRequest: ReportRequest): Response<Unit> {
        return reportService.postReport(reportRequest)
    }
}
