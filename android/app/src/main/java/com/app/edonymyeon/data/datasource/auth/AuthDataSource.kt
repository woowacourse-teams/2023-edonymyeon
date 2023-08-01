package com.app.edonymyeon.data.datasource.auth

import com.app.edonymyeon.data.dto.LoginDataModel
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
    }
}
