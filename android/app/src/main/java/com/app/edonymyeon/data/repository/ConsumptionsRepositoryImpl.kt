package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.datasource.consumptions.ConsumptionsDataSource
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toResult
import com.domain.edonymyeon.repository.ConsumptionsRepository
import javax.inject.Inject

class ConsumptionsRepositoryImpl @Inject constructor(
    private val consumptionsDataSource: ConsumptionsDataSource,
) : ConsumptionsRepository {
    override suspend fun getConsumptions(period: Int): Result<Any> {
        return consumptionsDataSource.getConsumptions(period).toResult { it, _ ->
            it.toDomain()
        }
    }
}
