package com.app.edonymyeon.data.datasource.post

import com.app.edonymyeon.data.dto.request.PostEditorRequest
import com.app.edonymyeon.data.dto.response.CommentsResponse
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.dto.response.PostEditorResponse
import com.app.edonymyeon.data.dto.response.Posts
import com.app.edonymyeon.data.service.PostService
import com.app.edonymyeon.data.service.client.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor() : PostDataSource {
    private val postService: PostService =
        RetrofitClient.getInstance().create(PostService::class.java)

    override suspend fun getPostDetail(
        postId: Long,
        notificationId: Long,
    ): Response<PostDetailResponse> {
        return postService.getPost(postId, notificationId)
    }

    override suspend fun deletePost(postId: Long): Response<Unit> {
        return postService.deletePost(postId)
    }

    override suspend fun getPosts(size: Int, page: Int): Response<Posts> {
        return postService.getPosts(size, page)
    }

    override suspend fun savePost(
        postEditorRequest: PostEditorRequest,
        imageFiles: List<File>,
    ): Response<PostEditorResponse> {
        val postEditorMap: HashMap<String, RequestBody> = hashMapOf()
        postEditorMap["title"] =
            postEditorRequest.title.createRequestBody()
        postEditorMap["content"] =
            postEditorRequest.content.createRequestBody()
        postEditorMap["price"] =
            postEditorRequest.price.toString().createRequestBody()
        val newImages = imageFiles.generateMultiPartFromFile()

        return postService.savePost(postEditorMap, newImages)
    }

    override suspend fun updatePost(
        id: Long,
        postEditorRequest: PostEditorRequest,
        imageUrls: List<String>,
        imageFiles: List<File>,
    ): Response<PostEditorResponse> {
        val postEditorMap: HashMap<String, RequestBody> = hashMapOf()
        postEditorMap["title"] =
            postEditorRequest.title.createRequestBody()
        postEditorMap["content"] =
            postEditorRequest.content.createRequestBody()
        postEditorMap["price"] =
            postEditorRequest.price.toString().createRequestBody()
        val originalImages = imageUrls.generateMultiPartFromUrl()
        val newImages = imageFiles.generateMultiPartFromFile()

        return postService.updatePost(id, postEditorMap, originalImages, newImages)
    }

    override suspend fun getHotPosts(): Response<Posts> {
        return postService.getHotPosts()
    }

    override suspend fun getComments(postId: Long): Response<CommentsResponse> {
        return postService.getComments(postId)
    }

    override suspend fun postComment(id: Long, image: File?, comment: String): Response<Unit> {
        val requestFile = image?.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartFile =
            requestFile?.let { MultipartBody.Part.createFormData("image", image.name, it) }
        val requestBody = comment.createRequestBody()
        return postService.postComment(id, if (image == null) null else multipartFile, requestBody)
    }

    override suspend fun deleteComment(postId: Long, commentId: Long): Response<Unit> {
        return postService.deleteComment(postId, commentId)
    }

    private fun List<String>.generateMultiPartFromUrl() =
        this.map {
            val requestBody = it.createRequestBody()
            MultipartBody.Part.createFormData("originalImages", null, requestBody)
        }

    private fun List<File>.generateMultiPartFromFile() =
        this.map { imgFile ->
            val requestFile = imgFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("newImages", imgFile.name, requestFile)
        }

    private fun String.createRequestBody() = this.toRequestBody("text/plain".toMediaTypeOrNull())
}
