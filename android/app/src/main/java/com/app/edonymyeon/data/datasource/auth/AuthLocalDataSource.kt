package com.app.edonymyeon.data.datasource.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AuthLocalDataSource private constructor(context: Context) : AuthDataSource() {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences(
        context,
        AUTH_INFO,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override fun getAuthToken(): String? {
        return sharedPreferences.getString(USER_ACCESS_TOKEN, "")
    }

    override fun setAuthToken(token: String) {
        sharedPreferences.edit().putString(USER_ACCESS_TOKEN, token).apply()
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
