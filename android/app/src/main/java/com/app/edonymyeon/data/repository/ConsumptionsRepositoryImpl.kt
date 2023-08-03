package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.consumptions.ConsumptionsDataSource
import com.app.edonymyeon.data.dto.response.ConsumptionsResponse
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.repository.ConsumptionsRepository
import org.json.JSONObject

class ConsumptionsRepositoryImpl(
    private val consumptionsDataSource: ConsumptionsDataSource,
) : ConsumptionsRepository {
    override suspend fun getConsumptions(period: Int): Result<Any> {
        val result = consumptionsDataSource.getConsumptions(period)

        return if (result.isSuccessful) {
            Result.success((result.body() as ConsumptionsResponse).toDomain())
        } else {
            val errorResponse = result.errorBody()?.string()
            val json = errorResponse?.let { JSONObject(it) }
            val errorMessage = json?.getString("errorMessage") ?: ""
            Result.failure(CustomThrowable(result.code(), errorMessage))
        }
    }
}
