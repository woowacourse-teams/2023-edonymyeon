package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.PostItem

interface PostRepository {
    suspend fun getPostDetail(postId: Long): Result<Any>
    suspend fun getPosts(
        size: Int,
        page: Int
    ): Result<List<PostItem>>

    suspend fun savePost(title: String, content: String, price: Int, images: List<String>): Result<Any>
    suspend fun updatePost(id: Long, title: String, content: String, price: Int, images: List<String>): Result<Any>
}
