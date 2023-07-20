package com.app.edonymyeon.data.repository

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

    override suspend fun savePost(
        title: String,
        content: String,
        price: Int,
        images: List<String>,
    ): Result<Any> {
        val result = postDataSource.savePost(title, content, price, images)
        return if (result.isSuccessful) {
            Result.success(result.body() as PostEditorResponse)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun updatePost(
        postId: Long,
        title: String,
        content: String,
        price: Int,
        images: List<String>,
    ): Result<Any> {
        val result = postDataSource.updatePost(postId, title, content, price, images)
        return if (result.isSuccessful) {
            Result.success(result.body() as PostEditorResponse)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
