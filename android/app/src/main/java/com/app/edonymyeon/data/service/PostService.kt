package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.response.CommentsResponse
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.dto.response.PostEditorResponse
import com.app.edonymyeon.data.dto.response.Posts
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
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
    suspend fun getPost(
        @Path("postId") postId: Long,
        @Query("notificated") notificated: Long,
    ): ApiResponse<PostDetailResponse>

    @DELETE("/posts/{postId}")
    suspend fun deletePost(@Path("postId") postId: Long): ApiResponse<Unit>

    @GET("/posts")
    suspend fun getPosts(
        @Query("size") size: Int,
        @Query("page") page: Int,
    ): ApiResponse<Posts>

    @Multipart
    @POST("/posts")
    suspend fun savePost(
        @PartMap postEditorRequest: HashMap<String, RequestBody>,
        @Part newImages: List<MultipartBody.Part>,
    ): ApiResponse<PostEditorResponse>

    @Multipart
    @PUT("/posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: Long,
        @PartMap postEditorRequest: HashMap<String, RequestBody>,
        @Part originalImages: List<MultipartBody.Part>,
        @Part newImages: List<MultipartBody.Part>,
    ): ApiResponse<PostEditorResponse>

    @GET("/posts/hot")
    suspend fun getHotPosts(): ApiResponse<Posts>

    @GET("/posts/{postId}/comments")
    suspend fun getComments(
        @Path("postId") postId: Long,
    ): ApiResponse<CommentsResponse>

    @Multipart
    @POST("/posts/{postId}/comments")
    suspend fun postComment(
        @Path("postId") postId: Long,
        @Part image: MultipartBody.Part?,
        @Part("comment") comment: RequestBody,
    ): ApiResponse<Unit>

    @DELETE("/posts/{postId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("postId") postId: Long,
        @Path("commentId") commentId: Long,
    ): ApiResponse<Unit>
}
