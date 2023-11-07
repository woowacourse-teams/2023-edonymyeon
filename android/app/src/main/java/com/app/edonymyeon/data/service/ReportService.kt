package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.common.ApiResponse
import com.app.edonymyeon.data.dto.request.ReportRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("report")
    suspend fun postReport(
        @Body reportRequest: ReportRequest,
    ): ApiResponse<Unit>
}
