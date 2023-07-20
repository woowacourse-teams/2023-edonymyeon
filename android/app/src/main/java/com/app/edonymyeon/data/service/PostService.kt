package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.dto.response.Posts
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {
    @GET("/posts/{postId}")
    suspend fun getPost(@Path("postId") postId: Long): Response<PostDetailResponse>

    @GET("/posts")
    suspend fun getPosts(
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Response<Posts>
}
