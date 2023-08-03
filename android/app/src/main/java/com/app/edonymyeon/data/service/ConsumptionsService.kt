package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.response.ConsumptionsResponse
import retrofit2.Response
import retrofit2.http.GET

interface ConsumptionsService {
    @GET("/consumptions")
    suspend fun getConsumptions(): Response<ConsumptionsResponse>
}
