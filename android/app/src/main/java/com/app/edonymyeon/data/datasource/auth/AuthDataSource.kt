package com.app.edonymyeon.data.datasource.auth

import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.dto.request.UserRegistrationRequestBody
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import retrofit2.Response

interface AuthDataSource {

    interface Local {
        fun getAuthToken(): String?
        fun setAuthToken(token: String)
    }

    interface Remote {
        suspend fun login(
            loginDataModel: LoginDataModel,
        ): Response<Unit>
        suspend fun signUp(userRegistrationRequestBody: UserRegistrationRequestBody): Response<Unit>
        suspend fun checkDuplicate(target: String, value: String): Response<AuthDuplicateResponse>
    }
}
