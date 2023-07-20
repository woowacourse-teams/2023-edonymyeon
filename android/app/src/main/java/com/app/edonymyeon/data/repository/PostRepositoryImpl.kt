package com.app.edonymyeon.data.repository

import android.net.Uri
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.post.PostDataSource
import com.app.edonymyeon.data.dto.request.PostEditorResponse
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.repository.PostRepository

class PostRepositoryImpl(private val postDataSource: PostDataSource) : PostRepository {
    override suspend fun getPostDetail(postId: Long): Result<Any> {
        val result = postDataSource.getPostDetail(postId)
        return if (result.isSuccessful) {
            Result.success((result.body() as PostDetailResponse).toDomain())
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun postPost(
        title: String,
        content: String,
        price: Int,
        images: List<String>,
    ): Result<Any> {
        val result = postDataSource.postPost(title, content, price, images.map { Uri.parse(it) })
        return if (result.isSuccessful) {
            Result.success(result.body() as PostEditorResponse)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun putPost(
        postId: Long,
        title: String,
        content: String,
        price: Int,
        images: List<String>,
    ): Result<Any> {
        val result = postDataSource.putPost(postId, title, content, price, images.map { Uri.parse(it) })
        return if (result.isSuccessful) {
            Result.success(result.body() as PostEditorResponse)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
