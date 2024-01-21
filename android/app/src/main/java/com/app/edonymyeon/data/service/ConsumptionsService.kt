package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.response.ConsumptionsResponse
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ConsumptionsService {
    @GET("/consumptions")
    suspend fun getConsumptions(@Query("period-month") period: Int): ApiResponse<ConsumptionsResponse>
}
