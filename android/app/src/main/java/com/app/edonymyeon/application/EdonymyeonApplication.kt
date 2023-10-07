package com.app.edonymyeon.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import app.edonymyeon.BuildConfig.KAKAO_APP_KEY
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.service.client.AccessTokenInterceptor
import com.app.edonymyeon.data.util.TokenSharedPreference
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EdonymyeonApplication : Application() {
    @Inject
    lateinit var sharedPreference: TokenSharedPreference

    override fun onCreate() {
        super.onCreate()

        AccessTokenInterceptor.setToken(
            sharedPreference.getValue(AuthLocalDataSource.USER_ACCESS_TOKEN),
        )

        KakaoSdk.init(this, KAKAO_APP_KEY)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
