package com.app.edonymyeon.data.datasource.auth

import com.app.edonymyeon.data.util.PreferenceUtil
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor() : AuthDataSource.Local {

    override fun getAuthToken(): String? {
        return PreferenceUtil.getValue(USER_ACCESS_TOKEN)
    }

    override fun setAuthToken(token: String) {
        PreferenceUtil.setValue(USER_ACCESS_TOKEN, token)
    }

    companion object {
        const val AUTH_INFO = "AUTH_INFO"
        const val USER_ACCESS_TOKEN = "USER_ACCESS_TOKEN"
    }
}
