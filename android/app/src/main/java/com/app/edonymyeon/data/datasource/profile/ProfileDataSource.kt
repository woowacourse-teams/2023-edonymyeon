package com.app.edonymyeon.data.datasource.profile

import com.app.edonymyeon.data.dto.WriterDataModel
import com.app.edonymyeon.data.dto.request.ProfileUpdateRequest
import com.app.edonymyeon.data.dto.request.PurchaseConfirmRequest
import com.app.edonymyeon.data.dto.request.SavingConfirmRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.data.dto.response.MyPostsResponse
import retrofit2.Response
import java.io.File

interface ProfileDataSource {
    suspend fun getMyPosts(page: Int, notificationId: Long): Response<MyPostsResponse>
    suspend fun postPurchaseConfirm(
        id: Long,
        purchaseConfirmRequest: PurchaseConfirmRequest,
    ): Response<Unit>

    suspend fun postSavingConfirm(
        id: Long,
        savingConfirmRequest: SavingConfirmRequest,
    ): Response<Unit>

    suspend fun deleteConfirm(id: Long): Response<Unit>
    suspend fun getProfile(): Response<WriterDataModel>
    suspend fun withdraw(): Response<Unit>

    suspend fun updateProfile(
        profileRequest: ProfileUpdateRequest,
        newProfileImage: File?,
    ): Response<Unit>

    suspend fun checkDuplicate(target: String, value: String): Response<AuthDuplicateResponse>
}
