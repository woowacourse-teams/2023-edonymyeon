package com.app.edonymyeon.data.datasource.recommend

import com.app.edonymyeon.data.service.RecommendService
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import javax.inject.Inject

class RecommendRemoteDataSource @Inject constructor(
    private val recommendService: RecommendService,
) : RecommendDataSource {

    override suspend fun saveRecommendUp(postId: Long): ApiResponse<Unit> {
        return recommendService.saveRecommendUp(postId)
    }

    override suspend fun deleteRecommendUp(postId: Long): ApiResponse<Unit> {
        return recommendService.deleteRecommendUp(postId)
    }

    override suspend fun saveRecommendDown(postId: Long): ApiResponse<Unit> {
        return recommendService.saveRecommendDown(postId)
    }

    override suspend fun deleteRecommendDown(postId: Long): ApiResponse<Unit> {
        return recommendService.deleteRecommendDown(postId)
    }
}
