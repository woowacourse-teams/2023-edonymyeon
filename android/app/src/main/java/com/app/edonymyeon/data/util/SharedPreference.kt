// package com.app.edonymyeon.data.util
//
// import android.content.Context
// import androidx.security.crypto.EncryptedSharedPreferences
// import androidx.security.crypto.MasterKey
// import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
//
// object SharedPreference {
//    var masterKey: MasterKey? = null
//    var sharedPreferences: EncryptedSharedPreferences? = null
//
//    fun init(context: Context) {
//        sharedPreferences = EncryptedSharedPreferences(
//            this,
//            AuthLocalDataSource.AUTH_INFO,
//            masterKey,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
//        )
//    }
//
//    fun initMasterKey(context: Context) {
//        val masterKey by lazy {
//            MasterKey.Builder(context)
//                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//                .build()
//        }
//    }
//
//    fun getToken() {
//    }
// }
