package com.app.edonymyeon.data.datasource.auth

import android.util.Log
import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.dto.request.LogoutRequest
import com.app.edonymyeon.data.dto.request.TokenRequest
import com.app.edonymyeon.data.dto.request.UserRegistrationRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.data.service.AuthService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor() : AuthDataSource.Remote {
    private val authService: AuthService =
        RetrofitClient.getInstance().create(AuthService::class.java)

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
        Log.d("testLog", logoutRequest.deviceToken)
        return authService.logout(logoutRequest)
    }

    override suspend fun loginByKakao(accessToken: TokenRequest): Response<Unit> {
        return authService.loginByKakao(accessToken)
    }
}
