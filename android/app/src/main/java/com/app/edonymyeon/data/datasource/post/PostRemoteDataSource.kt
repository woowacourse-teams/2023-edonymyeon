package com.app.edonymyeon.data.datasource.post

import com.app.edonymyeon.data.dto.request.PostEditorResponse
import com.app.edonymyeon.data.dto.response.PostDetailResponse
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

    init {
        RetrofitClient.getInstance()
            .updateAccessToken("Basic YmVhdXRpZnVsbmVvQG5hdmVyLmNvbTpuZW8xMjM=")
    }

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
        val images = generateMultiPart(imageUris)
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
        val images = generateMultiPart(imageUris)
        val postEditorMap: HashMap<String, RequestBody> = hashMapOf()
        postEditorMap["title"] = title.toRequestBody("text/plain".toMediaTypeOrNull())
        postEditorMap["content"] = content.toRequestBody("text/plain".toMediaTypeOrNull())
        postEditorMap["price"] = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        return postService.updatePost(id, postEditorMap, images)
    }

    private fun generateMultiPart(imageUris: List<String>) =
        imageUris.map { imageUri ->
            val file = File(imageUri)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("images", file.name, requestFile)
        }
}
