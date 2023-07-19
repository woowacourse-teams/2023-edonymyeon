package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.response.PostDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PostService {
    @GET("/posts/{postId}")
    suspend fun getPost(@Path("postId") postId: Long): Response<PostDetailResponse>
}