package com.app.edonymyeon.data.datasource.post

import com.app.edonymyeon.data.dto.response.PostDetailResponse

interface PostDataSource {
    suspend fun getPostDetail(postId: Long): Call<PostDetailResponse>
}
