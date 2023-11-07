package com.app.edonymyeon.data.datasource.recommend

import com.app.edonymyeon.data.common.ApiResponse

interface RecommendDataSource {
    suspend fun saveRecommendUp(postId: Long): ApiResponse<Unit>
    suspend fun deleteRecommendUp(postId: Long): ApiResponse<Unit>
    suspend fun saveRecommendDown(postId: Long): ApiResponse<Unit>
    suspend fun deleteRecommendDown(postId: Long): ApiResponse<Unit>
}
