package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.PostEditor
import com.domain.edonymyeon.model.PostItems
import java.io.File

interface PostRepository {
    suspend fun getPostDetail(postId: Long): Result<Any>
    suspend fun deletePost(postId: Long): Result<Any>

    suspend fun savePost(
        postEditor: PostEditor,
        images: List<File>,
    ): Result<Any>

    suspend fun getPosts(
        size: Int,
        page: Int,
    ): Result<PostItems>

    suspend fun updatePost(
        id: Long,
        postEditor: PostEditor,
        imageUrls: List<String>,
        imageFiles: List<File>,
    ): Result<Any>

    suspend fun getHotPosts(): Result<PostItems>

    suspend fun postComment(
        id: Long,
        images: File,
        contents: String,
    ): Result<Unit>

    suspend fun deleteComment(
        postId: Long,
        commentId: Long,
    ): Result<Unit>
}
