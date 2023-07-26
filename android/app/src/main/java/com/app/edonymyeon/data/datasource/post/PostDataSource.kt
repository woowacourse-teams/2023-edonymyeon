package com.app.edonymyeon.data.datasource.post

import com.app.edonymyeon.data.dto.request.PostEditorResponse
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.dto.response.Posts
import retrofit2.Response

interface PostDataSource {
    suspend fun getPostDetail(postId: Long): Response<PostDetailResponse>
    suspend fun deletePost(postId: Long): Response<Unit>
    suspend fun getPosts(size: Int, page: Int): Response<Posts>

    suspend fun savePost(
        title: String,
        content: String,
        price: Int,
        images: List<String>,
    ): Response<PostEditorResponse>

    suspend fun updatePost(
        id: Long,
        title: String,
        content: String,
        price: Int,
        Images: List<String>,
    ): Response<PostEditorResponse>
}
