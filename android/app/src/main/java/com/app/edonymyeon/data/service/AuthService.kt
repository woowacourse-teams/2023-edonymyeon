package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.dto.request.TokenRequest
import com.app.edonymyeon.data.dto.request.UserRegistrationRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("/join")
    suspend fun signUp(@Body userRegistrationRequest: UserRegistrationRequest): Response<Unit>

    @GET("/join")
    suspend fun checkDuplicate(
        @Query("target") target: String,
        @Query("value") value: String,
    ): Response<AuthDuplicateResponse>

    @POST("/login")
    suspend fun login(
        @Body loginDataModel: LoginDataModel,
    ): Response<Unit>

    @POST("/auth/kakao/login")
    suspend fun loginByKakao(
        @Body accessToken: TokenRequest,
    ): Response<Unit>
}
