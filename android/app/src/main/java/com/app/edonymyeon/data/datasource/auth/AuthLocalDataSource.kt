package com.app.edonymyeon.data.datasource.auth

import android.content.SharedPreferences

class AuthLocalDataSource private constructor(private val sharedPreferences: SharedPreferences) :
    AuthDataSource() {

    override fun getAuthToken(): String? {
        return sharedPreferences.getString(USER_ACCESS_TOKEN, "")
    }

    override fun setAuthToken(token: String) {
        sharedPreferences.edit().putString(USER_ACCESS_TOKEN, token).apply()
    }

    companion object {

        const val AUTH_INFO = "AUTH_INFO"
        private const val USER_ACCESS_TOKEN = "USER_ACCESS_TOKEN"

        private val authDataSource: AuthLocalDataSource? = null
        fun getInstance(sharedPreferences: SharedPreferences): AuthLocalDataSource {
            return authDataSource ?: synchronized(this) {
                AuthLocalDataSource(sharedPreferences)
            }
        }
    }
}
