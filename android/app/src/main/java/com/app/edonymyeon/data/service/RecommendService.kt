package com.app.edonymyeon.data.service

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface RecommendService {
    @PUT("/posts/{postId}/up")
    suspend fun saveRecommendUp(@Path("postId") postId: Long): Response<Unit>

    @DELETE("/posts/{postId}/up")
    suspend fun deleteRecommendUp(@Path("postId") postId: Long): Response<Unit>

    @PUT("/posts/{postId}/down")
    suspend fun saveRecommendDown(@Path("postId") postId: Long): Response<Unit>

    @DELETE("/posts/{postId}/down")
    suspend fun deleteRecommendDown(@Path("postId") postId: Long): Response<Unit>
}
