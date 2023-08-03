package com.domain.edonymyeon.repository

interface ConsumptionsRepository {
    suspend fun getConsumptions(): Result<Any>
}
