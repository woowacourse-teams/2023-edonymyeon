package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.post.PostDataSource
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.dto.response.Posts
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.model.PostItem
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

    override suspend fun getPosts(size: Int, page: Int): Result<List<PostItem>> {
        val result = postDataSource.getPosts(size, page)
        return if (result.isSuccessful) {
            Result.success((result.body() as Posts).post.map { it.toDomain() })
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}

