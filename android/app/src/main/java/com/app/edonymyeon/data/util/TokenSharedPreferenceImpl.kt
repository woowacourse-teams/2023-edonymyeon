package com.app.edonymyeon.data.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenSharedPreferenceImpl @Inject constructor(@ApplicationContext context: Context) : TokenSharedPreference {
    private val sharedPreferences: SharedPreferences

    init {
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

    override fun getValue(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun setValue(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}
