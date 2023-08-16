package com.app.edonymyeon.data.datasource.post

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
        title: String,
        content: String,
        price: Int,
        imageUris: List<String>,
    ): Response<PostEditorResponse> {
        val images = generateMultiPartFromUri(imageUris)
        val postEditorMap: HashMap<String, RequestBody> = hashMapOf()
        postEditorMap["title"] = title.toRequestBody("text/plain".toMediaTypeOrNull())
        postEditorMap["content"] = content.toRequestBody("text/plain".toMediaTypeOrNull())
        postEditorMap["price"] = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        return postService.savePost(postEditorMap, images)
    }

    override suspend fun updatePost(
        id: Long,
        title: String,
        content: String,
        price: Int,
        imageUris: List<String>,
    ): Response<PostEditorResponse> {
        val images = generateMultiPartFromUri(imageUris)
        val postEditorMap: HashMap<String, RequestBody> = hashMapOf()
        postEditorMap["title"] = title.toRequestBody("text/plain".toMediaTypeOrNull())
        postEditorMap["content"] = content.toRequestBody("text/plain".toMediaTypeOrNull())
        postEditorMap["price"] = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val originalImages = generateMultiPartFromUrl(imageUris)

        return postService.updatePost(id, postEditorMap, originalImages, images)
    }

    override suspend fun getHotPosts(): Response<Posts> {
        return postService.getHotPosts()
    }

    private fun generateMultiPartFromUrl(imageUris: List<String>) =
        imageUris.filter { it.startsWith("http") }.map {
            val requestBody = it.toRequestBody("text/plain".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("originalImages", null, requestBody)
        }

    private fun generateMultiPartFromUri(imageUris: List<String>) =
        imageUris.filter { it.startsWith("http").not() }.map { imageUri ->
            val file = File(imageUri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("newImages", file.name, requestFile)
        }
}
