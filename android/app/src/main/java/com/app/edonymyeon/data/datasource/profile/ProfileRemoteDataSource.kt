package com.app.edonymyeon.data.datasource.profile

import com.app.edonymyeon.data.dto.request.PurchaseConfirmRequest
import com.app.edonymyeon.data.dto.request.SavingConfirmRequest
import com.app.edonymyeon.data.dto.response.MyPostsResponse
import com.app.edonymyeon.data.dto.response.ProfileResponse
import com.app.edonymyeon.data.service.ProfileService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor() : ProfileDataSource {

    private val profileService: ProfileService =
        RetrofitClient.getInstance().create(ProfileService::class.java)

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

    override suspend fun getProfile(): Response<ProfileResponse> {
        return profileService.getProfile()
    }

    override suspend fun withdraw(): Response<Unit> {
        return profileService.withdraw()
    }
}
