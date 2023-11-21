package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.datasource.recommend.RecommendDataSource
import com.app.edonymyeon.mapper.toResult
import com.domain.edonymyeon.repository.RecommendRepository
import javax.inject.Inject

class RecommendRepositoryImpl @Inject constructor(
    private val recommendDataSource: RecommendDataSource,
) : RecommendRepository {
    override suspend fun saveRecommendUp(postId: Long): Result<Any> {
        return recommendDataSource.saveRecommendUp(postId).toResult()
    }

    override suspend fun deleteRecommendUp(postId: Long): Result<Any> {
        return recommendDataSource.deleteRecommendUp(postId).toResult()
    }

    override suspend fun saveRecommendDown(postId: Long): Result<Any> {
        return recommendDataSource.saveRecommendDown(postId).toResult()
    }

    override suspend fun deleteRecommendDown(postId: Long): Result<Any> {
        return recommendDataSource.deleteRecommendDown(postId).toResult()
    }
}
