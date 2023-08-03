package com.app.edonymyeon.data.datasource.profile

import com.app.edonymyeon.data.dto.response.ProfileResponse
import retrofit2.Response

interface ProfileDataSource {
    suspend fun getProfile(): Response<ProfileResponse>
}
