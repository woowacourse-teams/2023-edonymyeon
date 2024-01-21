package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.request.ReportRequest
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("report")
    suspend fun postReport(
        @Body reportRequest: ReportRequest,
    ): ApiResponse<Unit>
}
