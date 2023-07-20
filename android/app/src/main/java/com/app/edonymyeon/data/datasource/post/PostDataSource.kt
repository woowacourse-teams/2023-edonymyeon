package com.app.edonymyeon.data.datasource.post

import android.net.Uri
import com.app.edonymyeon.data.dto.request.PostEditorResponse
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import retrofit2.Response

interface PostDataSource {
    suspend fun getPostDetail(postId: Long): Response<PostDetailResponse>

    suspend fun savePost(
        title: String,
        content: String,
        price: Int,
        images: List<Uri>,
    ): Response<PostEditorResponse>

    suspend fun updatePost(
        id: Long,
        title: String,
        content: String,
        price: Int,
        Images: List<Uri>,
    ): Response<PostEditorResponse>
}
