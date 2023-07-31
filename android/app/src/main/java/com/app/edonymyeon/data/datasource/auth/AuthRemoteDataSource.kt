package com.app.edonymyeon.data.datasource.auth

import com.app.edonymyeon.data.dto.request.UserRegistrationRequestBody
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.data.service.AuthService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response

class AuthRemoteDataSource : AuthDataSource {
    private val authService: AuthService =
        RetrofitClient.getInstance().create(AuthService::class.java)

    override suspend fun signUp(userRegistrationRequestBody: UserRegistrationRequestBody): Response<Unit> {
        return authService.signUp(userRegistrationRequestBody)
    }

    override suspend fun checkDuplicate(
        target: String,
        value: String,
    ): Response<AuthDuplicateResponse> {
        return authService.checkDuplicate(target, value)
    }
}
