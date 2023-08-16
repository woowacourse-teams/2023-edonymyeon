package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.PostItems

interface PostRepository {
    suspend fun getPostDetail(postId: Long): Result<Any>
    suspend fun deletePost(postId: Long): Result<Any>

    suspend fun savePost(
        title: String,
        content: String,
        price: Int,
        images: List<String>,
    ): Result<Any>

    suspend fun getPosts(
        size: Int,
        page: Int,
    ): Result<PostItems>

    suspend fun updatePost(
        id: Long,
        title: String,
        content: String,
        price: Int,
        images: List<String>,
    ): Result<Any>

    suspend fun getHotPosts(): Result<PostItems>
}
