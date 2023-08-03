package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.consumptions.ConsumptionsDataSource
import com.app.edonymyeon.data.dto.response.ConsumptionsResponse
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.repository.ConsumptionsRepository

class ConsumptionsRepositoryImpl(
    private val consumptionsDataSource: ConsumptionsDataSource,
) : ConsumptionsRepository {
    override suspend fun getConsumptions(): Result<Any> {
        val result = consumptionsDataSource.getConsumptions()

        return if (result.isSuccessful) {
            Result.success((result.body() as ConsumptionsResponse).toDomain())
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
