package com.domain.edonymyeon.repository

interface PostRepository {
    suspend fun getPostDetail(postId: Long): Result<Any>

    suspend fun postPost(title: String, content: String, price: Int, images: List<String>): Result<Any>
    suspend fun putPost(id: Long, title: String, content: String, price: Int, images: List<String>): Result<Any>
}
