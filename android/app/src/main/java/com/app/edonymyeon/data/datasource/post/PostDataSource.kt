package com.app.edonymyeon.data.datasource.post

import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.dto.response.Posts
import retrofit2.Response

interface PostDataSource {
    suspend fun getPostDetail(postId: Long): Response<PostDetailResponse>

    suspend fun getPosts(size: Int, page: Int): Response<Posts>
}
