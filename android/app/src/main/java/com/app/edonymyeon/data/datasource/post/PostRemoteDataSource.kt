package com.app.edonymyeon.data.datasource.post

import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.service.PostService
import com.app.edonymyeon.data.service.client.RetrofitClient
import okhttp3.ResponseBody

class PostRemoteDataSource : PostDataSource {
    private val postService: PostService =
        RetrofitClient.getInstance().create(PostService::class.java)

    override suspend fun getPostDetail(postId: Long): Call<PostDetailResponse> {
        val result = postService.getPost(postId)
        return if (result.isSuccessful) {
            Call.Success(result.body())
        } else {
            Call.Error(result.errorBody())
        }
    }
}

sealed class Call<T> {
    data class Success<T>(val data: T?) : Call<T>()
    data class Error<T>(val errorResult: ResponseBody?) : Call<T>()
}
