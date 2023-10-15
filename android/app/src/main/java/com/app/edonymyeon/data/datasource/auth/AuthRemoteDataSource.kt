package com.app.edonymyeon.data.datasource.auth

import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.dto.request.LogoutRequest
import com.app.edonymyeon.data.dto.request.TokenRequest
import com.app.edonymyeon.data.dto.request.UserRegistrationRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.data.service.AuthService
import retrofit2.Response
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authService: AuthService,
) : AuthDataSource.Remote {

    override suspend fun signUp(userRegistrationRequest: UserRegistrationRequest): Response<Unit> {
        return authService.signUp(userRegistrationRequest)
    }

    override suspend fun checkDuplicate(
        target: String,
        value: String,
    ): Response<AuthDuplicateResponse> {
        return authService.checkDuplicate(target, value)
    }

    override suspend fun login(loginDataModel: LoginDataModel): Response<Unit> {
        return authService.login(loginDataModel)
    }

    override suspend fun logout(logoutRequest: LogoutRequest): Response<Unit> {
        return authService.logout(logoutRequest)
    }

    override suspend fun loginByKakao(accessToken: TokenRequest): Response<Unit> {
        return authService.loginByKakao(accessToken)
    }
}
