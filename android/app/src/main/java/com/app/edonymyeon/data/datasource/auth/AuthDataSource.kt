package com.app.edonymyeon.data.datasource.auth

import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.dto.request.LogoutRequest
import com.app.edonymyeon.data.dto.request.TokenRequest
import com.app.edonymyeon.data.dto.request.UserRegistrationRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse

interface AuthDataSource {

    interface Local {
        fun getAuthToken(): String?
        fun setAuthToken(token: String?)
    }

    interface Remote {
        suspend fun login(
            loginDataModel: LoginDataModel,
        ): ApiResponse<Unit>

        suspend fun loginByKakao(accessToken: TokenRequest): ApiResponse<Unit>

        suspend fun signUp(userRegistrationRequest: UserRegistrationRequest): ApiResponse<Unit>
        suspend fun checkDuplicate(
            target: String,
            value: String,
        ): ApiResponse<AuthDuplicateResponse>

        suspend fun logout(logoutRequest: LogoutRequest): ApiResponse<Unit>
    }
}
