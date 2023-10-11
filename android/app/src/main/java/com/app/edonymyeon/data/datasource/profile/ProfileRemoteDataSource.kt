package com.app.edonymyeon.data.datasource.profile

import com.app.edonymyeon.data.dto.WriterDataModel
import com.app.edonymyeon.data.dto.request.ProfileUpdateRequest
import com.app.edonymyeon.data.dto.request.PurchaseConfirmRequest
import com.app.edonymyeon.data.dto.request.SavingConfirmRequest
import com.app.edonymyeon.data.dto.response.MyPostsResponse
import com.app.edonymyeon.data.service.ProfileService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val profileService: ProfileService,
) : ProfileDataSource {

    override suspend fun getMyPosts(page: Int, notificationId: Long): Response<MyPostsResponse> {
        return profileService.getMyPost(20, page, notificationId)
    }

    override suspend fun postPurchaseConfirm(
        id: Long,
        purchaseConfirmRequest: PurchaseConfirmRequest,
    ): Response<Unit> {
        return profileService.postPurchaseConfirm(id, purchaseConfirmRequest)
    }

    override suspend fun postSavingConfirm(
        id: Long,
        savingConfirmRequest: SavingConfirmRequest,
    ): Response<Unit> {
        return profileService.postSavingConfirm(id, savingConfirmRequest)
    }

    override suspend fun deleteConfirm(id: Long): Response<Unit> {
        return profileService.deleteConfirm(id)
    }

    override suspend fun getProfile(): Response<WriterDataModel> {
        return profileService.getProfile()
    }

    override suspend fun withdraw(): Response<Unit> {
        return profileService.withdraw()
    }

    override suspend fun updateProfile(
        profileRequest: ProfileUpdateRequest,
        newProfileImage: File?,
    ): Response<Unit> {
        val nickname = profileRequest.nickname.toRequestBody("text/plain".toMediaTypeOrNull())
        return profileService.updateProfile(
            nickname,
            profileRequest.isImageChanged,
            newProfileImage?.toMultipartBody(),
        )
    }

    private fun File?.toMultipartBody(): MultipartBody.Part? {
        if (this == null) return null

        val requestFile = this.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("profileImage", this.name, requestFile)
    }
}
