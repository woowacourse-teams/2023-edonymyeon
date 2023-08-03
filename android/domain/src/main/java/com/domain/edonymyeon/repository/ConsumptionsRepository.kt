package com.domain.edonymyeon.repository

interface ConsumptionsRepository {
    suspend fun getConsumptions(period: Int): Result<Any>
}
