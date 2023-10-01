package com.app.edonymyeon.data.datasource.auth

import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.dto.request.LogoutRequest
import com.app.edonymyeon.data.dto.request.TokenRequest
import com.app.edonymyeon.data.dto.request.UserRegistrationRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import retrofit2.Response

interface AuthDataSource {

    interface Local {
        fun getAuthToken(): String?
        fun setAuthToken(token: String?)
    }

    interface Remote {
        suspend fun login(
            loginDataModel: LoginDataModel,
        ): Response<Unit>

        suspend fun loginByKakao(accessToken: TokenRequest): Response<Unit>

        suspend fun signUp(userRegistrationRequest: UserRegistrationRequest): Response<Unit>
        suspend fun checkDuplicate(target: String, value: String): Response<AuthDuplicateResponse>
        suspend fun logout(logoutRequest: LogoutRequest): Response<Unit>
    }
}
