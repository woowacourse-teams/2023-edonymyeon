package com.app.edonymyeon.data.datasource.auth

import android.content.SharedPreferences
import com.app.edonymyeon.data.util.PreferenceUtil

class AuthLocalDataSource private constructor(private val sharedPreferences: SharedPreferences) :
    AuthDataSource.Local {

    override fun getAuthToken(): String? {
        return PreferenceUtil.getValue(USER_ACCESS_TOKEN)
    }

    override fun setAuthToken(token: String) {
        PreferenceUtil.setValue(USER_ACCESS_TOKEN, token)
    }

    companion object {
        const val AUTH_INFO = "AUTH_INFO"
        const val USER_ACCESS_TOKEN = "USER_ACCESS_TOKEN"

        private val authDataSource: AuthLocalDataSource? = null
        fun getInstance(sharedPreferences: SharedPreferences): AuthLocalDataSource {
            return authDataSource ?: synchronized(this) {
                AuthLocalDataSource(sharedPreferences)
            }
        }
    }
}
