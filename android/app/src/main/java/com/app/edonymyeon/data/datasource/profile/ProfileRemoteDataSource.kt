package com.app.edonymyeon.data.datasource.profile

import com.app.edonymyeon.data.dto.response.ProfileResponse
import com.app.edonymyeon.data.service.ProfileService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response

class ProfileRemoteDataSource : ProfileDataSource {
    private val profileService: ProfileService =
        RetrofitClient.getInstance().create(ProfileService::class.java)

    override suspend fun getProfile(): Response<ProfileResponse> {
        return profileService.getProfile()
    }
}
