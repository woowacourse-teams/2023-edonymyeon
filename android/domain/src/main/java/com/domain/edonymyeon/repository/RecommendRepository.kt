package com.domain.edonymyeon.repository

interface RecommendRepository {
    suspend fun saveRecommendUp(postId: Long): Result<Any>
    suspend fun deleteRecommendUp(postId: Long): Result<Any>
    suspend fun saveRecommendDown(postId: Long): Result<Any>
    suspend fun deleteRecommendDown(postId: Long): Result<Any>
}
