package com.app.edonymyeon.data.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource

object PreferenceUtil {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = EncryptedSharedPreferences(
            context,
            AuthLocalDataSource.AUTH_INFO,
            getMasterKey(context),
        )
    }

    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    fun getValue(key: String) {
        sharedPreferences.getString(key, "")
    }

    fun setValue(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}
