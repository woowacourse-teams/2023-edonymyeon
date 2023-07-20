package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.request.PostEditorRequest
import com.app.edonymyeon.data.dto.request.PostEditorResponse
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PostService {
    @GET("/posts/{postId}")
    suspend fun getPost(@Path("postId") postId: Long): Response<PostDetailResponse>

    @POST("/posts")
    suspend fun savePost(
        @Body postEditorRequest: PostEditorRequest,
    ): Response<PostEditorResponse>

    @PUT("/posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: Long,
        @Body postEditorRequest: PostEditorRequest,
    ): Response<PostEditorResponse>
}
