package com.app.edonymyeon.data.datasource.consumptions

import com.app.edonymyeon.data.dto.response.ConsumptionsResponse
import com.app.edonymyeon.data.service.ConsumptionsService
import retrofit2.Response
import javax.inject.Inject

class ConsumptionsRemoteDataSource @Inject constructor(
    private val consumptionsService: ConsumptionsService,
) : ConsumptionsDataSource {

    override suspend fun getConsumptions(period: Int): Response<ConsumptionsResponse> {
        return consumptionsService.getConsumptions(period)
    }
}
