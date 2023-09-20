package com.app.edonymyeon.data.datasource.consumptions

import com.app.edonymyeon.data.dto.response.ConsumptionsResponse
import com.app.edonymyeon.data.service.ConsumptionsService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response
import javax.inject.Inject

class ConsumptionsRemoteDataSource @Inject constructor() : ConsumptionsDataSource {
    private val consumptionsService: ConsumptionsService =
        RetrofitClient.getInstance().create(ConsumptionsService::class.java)

    override suspend fun getConsumptions(period: Int): Response<ConsumptionsResponse> {
        return consumptionsService.getConsumptions(period)
    }
}
