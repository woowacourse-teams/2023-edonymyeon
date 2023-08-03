package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProfileService {
    @GET("/profile")
    suspend fun getProfile(): Response<ProfileResponse>
}
