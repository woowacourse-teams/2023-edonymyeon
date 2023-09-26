package com.app.edonymyeon.data.datasource.auth

import com.app.edonymyeon.data.service.client.AccessTokenInterceptor
import com.app.edonymyeon.data.util.TokenSharedPreference
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val tokenSharedPreference: TokenSharedPreference,
) : AuthDataSource.Local {

    override fun getAuthToken(): String? {
        return tokenSharedPreference.getValue(USER_ACCESS_TOKEN)
    }

    override fun setAuthToken(token: String?) {
        tokenSharedPreference.setValue(USER_ACCESS_TOKEN, token)
        AccessTokenInterceptor.setToken(token)
    }

    companion object {
        const val AUTH_INFO = "AUTH_INFO"
        const val USER_ACCESS_TOKEN = "USER_ACCESS_TOKEN"
    }
}
