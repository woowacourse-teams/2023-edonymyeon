package com.app.edonymyeon.data.datasource.post

import com.app.edonymyeon.data.dto.request.PostEditorRequest
import com.app.edonymyeon.data.dto.response.CommentsResponse
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.dto.response.PostEditorResponse
import com.app.edonymyeon.data.dto.response.Posts
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import java.io.File

interface PostDataSource {
    suspend fun getPostDetail(
        postId: Long,
        notificationId: Long,
    ): ApiResponse<PostDetailResponse>

    suspend fun deletePost(postId: Long): ApiResponse<Unit>
    suspend fun getPosts(size: Int, page: Int): ApiResponse<Posts>

    suspend fun savePost(
        postEditorRequest: PostEditorRequest,
        imageFiles: List<File>,
    ): ApiResponse<PostEditorResponse>

    suspend fun updatePost(
        id: Long,
        postEditorRequest: PostEditorRequest,
        imageUrls: List<String>,
        imageFiles: List<File>,
    ): ApiResponse<PostEditorResponse>

    suspend fun getHotPosts(): ApiResponse<Posts>

    suspend fun getComments(postId: Long): ApiResponse<CommentsResponse>

    suspend fun postComment(
        id: Long,
        image: File?,
        comment: String,
    ): ApiResponse<Unit>

    suspend fun deleteComment(
        postId: Long,
        commentId: Long,
    ): ApiResponse<Unit>
}
