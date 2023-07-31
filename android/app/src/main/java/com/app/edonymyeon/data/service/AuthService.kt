package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.request.UserRegistrationRequestBody
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("/join")
    suspend fun signUp(@Body userRegistrationRequestBody: UserRegistrationRequestBody): Response<Unit>

    @GET("/join")
    suspend fun checkDuplicate(
        @Query("target") target: String,
        @Query("value") value: String,
    ): Response<AuthDuplicateResponse>
}
