package com.app.edonymyeon.data.datasource.post

import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.service.PostService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response

class PostRemoteDataSource : PostDataSource {
    private val postService: PostService =
        RetrofitClient.getInstance().create(PostService::class.java)

    override suspend fun getPostDetail(postId: Long): Response<PostDetailResponse> {
        return postService.getPost(postId)
    }

    override suspend fun deletePost(postId: Long): Response<Unit> {
        return postService.deletePost(postId)
    }
}
