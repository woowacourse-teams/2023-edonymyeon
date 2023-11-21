package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.dto.request.LogoutRequest
import com.app.edonymyeon.data.dto.request.TokenRequest
import com.app.edonymyeon.data.dto.request.UserRegistrationRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("/join")
    suspend fun signUp(@Body userRegistrationRequest: UserRegistrationRequest): ApiResponse<Unit>

    @GET("/join")
    suspend fun checkDuplicate(
        @Query("target") target: String,
        @Query("value") value: String,
    ): ApiResponse<AuthDuplicateResponse>

    @POST("/login")
    suspend fun login(
        @Body loginDataModel: LoginDataModel,
    ): ApiResponse<Unit>

    @POST("/auth/kakao/login")
    suspend fun loginByKakao(
        @Body accessToken: TokenRequest,
    ): ApiResponse<Unit>

    @POST("/logout")
    suspend fun logout(
        @Body logoutRequest: LogoutRequest,
    ): ApiResponse<Unit>
}
