package com.app.edonymyeon.data.datasource.post

import com.app.edonymyeon.data.dto.response.PostDetailResponse
import retrofit2.Response

interface PostDataSource {
    suspend fun getPostDetail(postId: Long): Response<PostDetailResponse>
}
