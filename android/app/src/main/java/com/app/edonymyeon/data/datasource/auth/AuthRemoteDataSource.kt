package com.app.edonymyeon.data.datasource.auth

import com.app.edonymyeon.data.common.ApiResponse
import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.dto.request.LogoutRequest
import com.app.edonymyeon.data.dto.request.TokenRequest
import com.app.edonymyeon.data.dto.request.UserRegistrationRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.data.service.AuthService
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authService: AuthService,
) : AuthDataSource.Remote {

    override suspend fun signUp(userRegistrationRequest: UserRegistrationRequest): ApiResponse<Unit> {
        return authService.signUp(userRegistrationRequest)
    }

    override suspend fun checkDuplicate(
        target: String,
        value: String,
    ): ApiResponse<AuthDuplicateResponse> {
        return authService.checkDuplicate(target, value)
    }

    override suspend fun login(loginDataModel: LoginDataModel): ApiResponse<Unit> {
        return authService.login(loginDataModel)
    }

    override suspend fun logout(logoutRequest: LogoutRequest): ApiResponse<Unit> {
        return authService.logout(logoutRequest)
    }

    override suspend fun loginByKakao(accessToken: TokenRequest): ApiResponse<Unit> {
        return authService.loginByKakao(accessToken)
    }
}
