package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.post.PostDataSource
import com.app.edonymyeon.data.dto.response.CommentsResponse
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.dto.response.PostEditorResponse
import com.app.edonymyeon.mapper.toDataModel
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.model.Comments
import com.domain.edonymyeon.model.PostEditor
import com.domain.edonymyeon.model.PostItems
import com.domain.edonymyeon.repository.PostRepository
import java.io.File

class PostRepositoryImpl(private val postDataSource: PostDataSource) : PostRepository {
    override suspend fun getPostDetail(postId: Long): Result<Any> {
        val result = postDataSource.getPostDetail(postId)
        return if (result.isSuccessful) {
            Result.success((result.body() as PostDetailResponse).toDomain())
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun deletePost(postId: Long): Result<Any> {
        val result = postDataSource.deletePost(postId)
        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun getPosts(size: Int, page: Int): Result<PostItems> {
        val result = postDataSource.getPosts(size, page)
        return if (result.isSuccessful && result.body() != null) {
            Result.success(
                PostItems(
                    result.body()!!.post.map {
                        it.toDomain()
                    },
                    result.body()!!.isLast,
                ),
            )
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun savePost(
        postEditor: PostEditor,
        images: List<File>,
    ): Result<Any> {
        val result = postDataSource.savePost(postEditor.toDataModel(), images)
        return if (result.isSuccessful) {
            Result.success(result.body() as PostEditorResponse)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun updatePost(
        postId: Long,
        postEditor: PostEditor,
        imageUrls: List<String>,
        imageFiles: List<File>,
    ): Result<Any> {
        val result =
            postDataSource.updatePost(postId, postEditor.toDataModel(), imageUrls, imageFiles)
        return if (result.isSuccessful) {
            Result.success(result.body() as PostEditorResponse)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun getHotPosts(): Result<PostItems> {
        val result = postDataSource.getHotPosts()
        return if (result.isSuccessful && result.body() != null) {
            Result.success(
                PostItems(
                    result.body()!!.post.map {
                        it.toDomain()
                    },
                    result.body()!!.isLast,
                ),
            )
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun getComments(postId: Long): Result<Comments> {
        val result = postDataSource.getComments(postId)
        return if (result.isSuccessful && result.body() != null) {
            val body = result.body()
            Result.success(
                Comments(
                    body?.commentCount ?: 0,
                    (body as CommentsResponse).comments.map {
                        it.toDomain()
                    },
                ),
            )
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun postComment(id: Long, images: File, contents: String): Result<Unit> {
        val result = postDataSource.postComment(id, images, contents)
        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun deleteComment(postId: Long, commentId: Long): Result<Unit> {
        val result = postDataSource.deleteComment(postId, commentId)
        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
