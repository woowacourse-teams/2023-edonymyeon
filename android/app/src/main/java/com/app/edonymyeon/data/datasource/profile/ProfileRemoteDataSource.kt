package com.app.edonymyeon.data.datasource.profile

import com.app.edonymyeon.data.dto.WriterDataModel
import com.app.edonymyeon.data.dto.request.ProfileUpdateRequest
import com.app.edonymyeon.data.dto.request.PurchaseConfirmRequest
import com.app.edonymyeon.data.dto.request.SavingConfirmRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.data.dto.response.MyPostsResponse
import com.app.edonymyeon.data.service.ProfileService
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val profileService: ProfileService,
) : ProfileDataSource {

    override suspend fun getMyPosts(page: Int, notificationId: Long): ApiResponse<MyPostsResponse> {
        return profileService.getMyPost(20, page, notificationId)
    }

    override suspend fun postPurchaseConfirm(
        id: Long,
        purchaseConfirmRequest: PurchaseConfirmRequest,
    ): ApiResponse<Unit> {
        return profileService.postPurchaseConfirm(id, purchaseConfirmRequest)
    }

    override suspend fun postSavingConfirm(
        id: Long,
        savingConfirmRequest: SavingConfirmRequest,
    ): ApiResponse<Unit> {
        return profileService.postSavingConfirm(id, savingConfirmRequest)
    }

    override suspend fun deleteConfirm(id: Long): ApiResponse<Unit> {
        return profileService.deleteConfirm(id)
    }

    override suspend fun getProfile(): ApiResponse<WriterDataModel> {
        return profileService.getProfile()
    }

    override suspend fun withdraw(): ApiResponse<Unit> {
        return profileService.withdraw()
    }

    override suspend fun updateProfile(
        profileRequest: ProfileUpdateRequest,
        newProfileImage: File?,
    ): ApiResponse<Unit> {
        val nickname = profileRequest.nickname.toRequestBody("text/plain".toMediaTypeOrNull())
        return profileService.updateProfile(
            nickname,
            profileRequest.isImageChanged,
            newProfileImage?.toMultipartBody(),
        )
    }

    override suspend fun checkDuplicate(
        target: String,
        value: String,
    ): ApiResponse<AuthDuplicateResponse> {
        return profileService.checkDuplicate(target, value)
    }

    private fun File?.toMultipartBody(): MultipartBody.Part? {
        if (this == null) return null

        val requestFile = this.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("profileImage", this.name, requestFile)
    }
}
