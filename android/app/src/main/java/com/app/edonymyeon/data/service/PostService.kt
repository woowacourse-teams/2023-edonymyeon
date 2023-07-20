package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.request.PostEditorResponse
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.dto.response.Posts
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
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

    @Multipart
    @POST("/posts")
    suspend fun savePost(
        @PartMap postEditorRequest: HashMap<String, RequestBody>,
        @Part images: List<MultipartBody.Part>,
    ): Response<PostEditorResponse>

    @Multipart
    @PUT("/posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: Long,
        @PartMap postEditorRequest: HashMap<String, RequestBody>,
        @Part images: List<MultipartBody.Part>,
    ): Response<PostEditorResponse>
}
