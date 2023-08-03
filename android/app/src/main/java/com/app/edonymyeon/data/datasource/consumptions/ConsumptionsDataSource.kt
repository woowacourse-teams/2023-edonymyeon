package com.app.edonymyeon.data.datasource.consumptions

import com.app.edonymyeon.data.dto.response.ConsumptionsResponse
import retrofit2.Response

interface ConsumptionsDataSource {
    suspend fun getConsumptions(): Response<ConsumptionsResponse>
}
