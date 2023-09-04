package com.app.edonymyeon.data.datasource.post

import com.app.edonymyeon.data.dto.request.PostEditorRequest
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.dto.response.PostEditorResponse
import com.app.edonymyeon.data.dto.response.Posts
import retrofit2.Response
import java.io.File

interface PostDataSource {
    suspend fun getPostDetail(postId: Long): Response<PostDetailResponse>
    suspend fun deletePost(postId: Long): Response<Unit>
    suspend fun getPosts(size: Int, page: Int): Response<Posts>

    suspend fun savePost(
        postEditorRequest: PostEditorRequest,
        imageFiles: List<File>,
    ): Response<PostEditorResponse>

    suspend fun updatePost(
        id: Long,
        postEditorRequest: PostEditorRequest,
        imageUrls: List<String>,
        imageFiles: List<File>,
    ): Response<PostEditorResponse>

    suspend fun getHotPosts(): Response<Posts>

    suspend fun postComment(
        id: Long,
        image: File,
        content: String,
    ): Response<Unit>
}
