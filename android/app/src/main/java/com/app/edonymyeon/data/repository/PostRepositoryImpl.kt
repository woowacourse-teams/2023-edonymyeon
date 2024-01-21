package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.datasource.post.PostDataSource
import com.app.edonymyeon.mapper.toDataModel
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toResult
import com.domain.edonymyeon.model.Comments
import com.domain.edonymyeon.model.PostEditor
import com.domain.edonymyeon.model.PostItems
import com.domain.edonymyeon.repository.PostRepository
import java.io.File
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDataSource: PostDataSource,
) : PostRepository {
    override suspend fun getPostDetail(postId: Long, notificationId: Long): Result<Any> {
        return postDataSource.getPostDetail(postId, notificationId).toResult { it, _ ->
            it.toDomain()
        }
    }

    override suspend fun deletePost(postId: Long): Result<Any> {
        return postDataSource.deletePost(postId).toResult()
    }

    override suspend fun getPosts(size: Int, page: Int): Result<PostItems> {
        return postDataSource.getPosts(size, page).toResult { postItems, _ ->
            PostItems(
                postItems.post.map {
                    it.toDomain()
                },
                postItems.isLast,
            )
        }
    }

    override suspend fun savePost(
        postEditor: PostEditor,
        images: List<File>,
    ): Result<Any> {
        return postDataSource.savePost(postEditor.toDataModel(), images).toResult()
    }

    override suspend fun updatePost(
        postId: Long,
        postEditor: PostEditor,
        imageUrls: List<String>,
        imageFiles: List<File>,
    ): Result<Any> {
        return postDataSource.updatePost(postId, postEditor.toDataModel(), imageUrls, imageFiles)
            .toResult()
    }

    override suspend fun getHotPosts(): Result<PostItems> {
        return postDataSource.getHotPosts().toResult { it, _ ->
            PostItems(
                it.post.map { post ->
                    post.toDomain()
                },
                it.isLast,
            )
        }
    }

    override suspend fun getComments(postId: Long): Result<Comments> {
        return postDataSource.getComments(postId).toResult { it, _ ->
            Comments(
                it.commentCount,
                it.comments.map { commentData ->
                    commentData.toDomain()
                },
            )
        }
    }

    override suspend fun postComment(id: Long, image: File?, comment: String): Result<Unit> {
        return postDataSource.postComment(id, image, comment).toResult()
    }

    override suspend fun deleteComment(postId: Long, commentId: Long): Result<Unit> {
        return postDataSource.deleteComment(postId, commentId).toResult()
    }
}
