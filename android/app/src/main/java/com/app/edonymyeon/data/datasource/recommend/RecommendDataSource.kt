package com.app.edonymyeon.data.datasource.recommend

import retrofit2.Response

interface RecommendDataSource {
    suspend fun saveRecommendUp(postId: Long): Response<Unit>
    suspend fun deleteRecommendUp(postId: Long): Response<Unit>
    suspend fun saveRecommendDown(postId: Long): Response<Unit>
    suspend fun deleteRecommendDown(postId: Long): Response<Unit>
}
