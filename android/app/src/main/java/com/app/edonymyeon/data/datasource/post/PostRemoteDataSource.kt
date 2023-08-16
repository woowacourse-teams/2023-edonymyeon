package com.app.edonymyeon.data.datasource.post

import com.app.edonymyeon.data.dto.request.PostEditorRequest
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

class PostRemoteDataSource : PostDataSource {
    private val postService: PostService =
        RetrofitClient.getInstance().create(PostService::class.java)

    override suspend fun getPostDetail(postId: Long): Response<PostDetailResponse> {
        return postService.getPost(postId)
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
            postEditorRequest.title.toRequestBody("text/plain".toMediaTypeOrNull())
        postEditorMap["content"] =
            postEditorRequest.content.toRequestBody("text/plain".toMediaTypeOrNull())
        postEditorMap["price"] =
            postEditorRequest.price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
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
            postEditorRequest.title.toRequestBody("text/plain".toMediaTypeOrNull())
        postEditorMap["content"] =
            postEditorRequest.content.toRequestBody("text/plain".toMediaTypeOrNull())
        postEditorMap["price"] =
            postEditorRequest.price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val originalImages = imageUrls.generateMultiPartFromUrl()
        val newImages = imageFiles.generateMultiPartFromFile()

        return postService.updatePost(id, postEditorMap, originalImages, newImages)
    }

    override suspend fun getHotPosts(): Response<Posts> {
        return postService.getHotPosts()
    }

    private fun List<String>.generateMultiPartFromUrl() =
        this.map {
            val requestBody = it.toRequestBody("text/plain".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("originalImages", null, requestBody)
        }

    private fun List<File>.generateMultiPartFromFile() =
        this.map { imgFile ->
            val requestFile = imgFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("newImages", imgFile.name, requestFile)
        }
}
