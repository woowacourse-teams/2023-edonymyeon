package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.recommend.RecommendDataSource
import com.domain.edonymyeon.repository.RecommendRepository

class RecommendRepositoryImpl(
    private val recommendDataSource: RecommendDataSource,
) : RecommendRepository {
    override suspend fun saveRecommendUp(postId: Long): Result<Any> {
        val result = recommendDataSource.saveRecommendUp(postId)

        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun deleteRecommendUp(postId: Long): Result<Any> {
        val result = recommendDataSource.deleteRecommendUp(postId)

        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun saveRecommendDown(postId: Long): Result<Any> {
        val result = recommendDataSource.saveRecommendDown(postId)

        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun deleteRecommendDown(postId: Long): Result<Any> {
        val result = recommendDataSource.deleteRecommendDown(postId)

        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
