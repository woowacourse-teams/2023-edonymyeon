package com.app.edonymyeon.data.datasource.consumptions

import com.app.edonymyeon.data.common.ApiResponse
import com.app.edonymyeon.data.dto.response.ConsumptionsResponse
import com.app.edonymyeon.data.service.ConsumptionsService
import javax.inject.Inject

class ConsumptionsRemoteDataSource @Inject constructor(
    private val consumptionsService: ConsumptionsService,
) : ConsumptionsDataSource {

    override suspend fun getConsumptions(period: Int): ApiResponse<ConsumptionsResponse> {
        return consumptionsService.getConsumptions(period)
    }
}
