package com.app.edonymyeon.data.datasource.profile

import com.app.edonymyeon.data.dto.request.PurchaseConfirmRequest
import com.app.edonymyeon.data.dto.request.SavingConfirmRequest
import com.app.edonymyeon.data.dto.response.MyPostsResponse
import com.app.edonymyeon.data.dto.response.ProfileResponse
import retrofit2.Response

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
    suspend fun getProfile(): Response<ProfileResponse>
    suspend fun withdraw(): Response<Unit>
}
