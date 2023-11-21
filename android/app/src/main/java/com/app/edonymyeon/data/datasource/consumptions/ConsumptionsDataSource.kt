package com.app.edonymyeon.data.datasource.consumptions

import com.app.edonymyeon.data.dto.response.ConsumptionsResponse
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse

interface ConsumptionsDataSource {
    suspend fun getConsumptions(period: Int): ApiResponse<ConsumptionsResponse>
}
