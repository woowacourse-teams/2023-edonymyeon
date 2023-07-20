package com.app.edonymyeon.data.datasource.post

import android.net.Uri
import com.app.edonymyeon.data.dto.request.PartWrapper
import com.app.edonymyeon.data.dto.request.PostEditorRequest
import com.app.edonymyeon.data.dto.request.PostEditorResponse
import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.data.service.PostService
import com.app.edonymyeon.data.service.client.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class PostRemoteDataSource : PostDataSource {
    private val postService: PostService =
        RetrofitClient.getInstance().create(PostService::class.java)

    override suspend fun getPostDetail(postId: Long): Response<PostDetailResponse> {
        return postService.getPost(postId)
    }

    override suspend fun postPost(
        title: String,
        content: String,
        price: Int,
        imageUris: List<Uri>,
    ): Response<PostEditorResponse> {
        val partWrappers = generatePartWrappers(imageUris)

        val postEditorRequest = PostEditorRequest(title, content, price, partWrappers)
        return postService.postPost(postEditorRequest)
    }

    override suspend fun putPost(
        id: Long,
        title: String,
        content: String,
        price: Int,
        imageUris: List<Uri>,
    ): Response<PostEditorResponse> {
        val partWrappers = generatePartWrappers(imageUris)
        val postEditorRequest = PostEditorRequest(title, content, price, partWrappers)
        return postService.putPost(id, postEditorRequest)
    }

    private fun generatePartWrappers(imageUris: List<Uri>) =
        imageUris.map { imageUri ->
            val file = File(imageUri.path ?: "")
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("images[]", file.name, requestFile)
            PartWrapper(part)
        }
}
