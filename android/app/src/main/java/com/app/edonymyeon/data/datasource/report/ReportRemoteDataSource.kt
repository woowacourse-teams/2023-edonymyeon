package com.app.edonymyeon.data.datasource.report

import com.app.edonymyeon.data.dto.request.ReportRequest
import com.app.edonymyeon.data.service.ReportService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response
import javax.inject.Inject

class ReportRemoteDataSource @Inject constructor() : ReportDataSource {

    private val reportService: ReportService =
        RetrofitClient.getInstance().create(ReportService::class.java)

    override suspend fun postReport(reportRequest: ReportRequest): Response<Unit> {
        return reportService.postReport(reportRequest)
    }
}
