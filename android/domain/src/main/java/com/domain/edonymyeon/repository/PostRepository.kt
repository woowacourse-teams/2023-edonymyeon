package com.domain.edonymyeon.repository

interface PostRepository {
    suspend fun getPostDetail(postId: Long): Result<Any>
}
