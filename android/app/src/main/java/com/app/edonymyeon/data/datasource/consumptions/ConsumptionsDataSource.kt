package com.app.edonymyeon.data.datasource.consumptions

import com.app.edonymyeon.data.common.ApiResponse
import com.app.edonymyeon.data.dto.response.ConsumptionsResponse

interface ConsumptionsDataSource {
    suspend fun getConsumptions(period: Int): ApiResponse<ConsumptionsResponse>
}
