package com.app.edonymyeon.data.datasource.recommend

import com.app.edonymyeon.data.service.RecommendService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response

class RecommendRemoteDataSource : RecommendDataSource {
    private val recommendService: RecommendService =
        RetrofitClient.getInstance().create(RecommendService::class.java)

    override suspend fun saveRecommendUp(postId: Long): Response<Unit> {
        return recommendService.saveRecommendUp(postId)
    }

    override suspend fun deleteRecommendUp(postId: Long): Response<Unit> {
        return recommendService.deleteRecommendUp(postId)
    }

    override suspend fun saveRecommendDown(postId: Long): Response<Unit> {
        return recommendService.saveRecommendDown(postId)
    }

    override suspend fun deleteRecommendDown(postId: Long): Response<Unit> {
        return recommendService.deleteRecommendDown(postId)
    }
}
