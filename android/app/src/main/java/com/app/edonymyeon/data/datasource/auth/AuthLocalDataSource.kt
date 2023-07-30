package com.app.edonymyeon.data.datasource.auth

import android.content.Context
import android.content.SharedPreferences

class AuthLocalDataSource private constructor(context: Context) : AuthDataSource {

    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences(AUTH_INFO, Context.MODE_PRIVATE)

    override fun getAuthToken(): String? {
        return sharedPreference.getString(USER_ACCESS_TOKEN, "")
    }

    override fun setAuthToken(token: String) {
        sharedPreference.edit().putString(USER_ACCESS_TOKEN, token).apply()
    }

    companion object {

        private const val AUTH_INFO = "AUTH_INFO"
        private const val USER_ACCESS_TOKEN = "USER_ACCESS_TOKEN"

        private val authDataSource: AuthLocalDataSource? = null
        fun getInstance(context: Context): AuthLocalDataSource {
            return authDataSource ?: synchronized(this) {
                AuthLocalDataSource(context)
            }
        }
    }
}
