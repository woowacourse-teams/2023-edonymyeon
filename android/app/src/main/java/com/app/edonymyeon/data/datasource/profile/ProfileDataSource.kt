package com.app.edonymyeon.data.datasource.profile

import com.app.edonymyeon.data.dto.WriterDataModel
import com.app.edonymyeon.data.dto.request.ProfileUpdateRequest
import com.app.edonymyeon.data.dto.request.PurchaseConfirmRequest
import com.app.edonymyeon.data.dto.request.SavingConfirmRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.data.dto.response.MyPostsResponse
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import java.io.File

interface ProfileDataSource {
    suspend fun getMyPosts(page: Int, notificationId: Long): ApiResponse<MyPostsResponse>
    suspend fun postPurchaseConfirm(
        id: Long,
        purchaseConfirmRequest: PurchaseConfirmRequest,
    ): ApiResponse<Unit>

    suspend fun postSavingConfirm(
        id: Long,
        savingConfirmRequest: SavingConfirmRequest,
    ): ApiResponse<Unit>

    suspend fun deleteConfirm(id: Long): ApiResponse<Unit>
    suspend fun getProfile(): ApiResponse<WriterDataModel>
    suspend fun withdraw(): ApiResponse<Unit>

    suspend fun updateProfile(
        profileRequest: ProfileUpdateRequest,
        newProfileImage: File?,
    ): ApiResponse<Unit>

    suspend fun checkDuplicate(target: String, value: String): ApiResponse<AuthDuplicateResponse>
}
