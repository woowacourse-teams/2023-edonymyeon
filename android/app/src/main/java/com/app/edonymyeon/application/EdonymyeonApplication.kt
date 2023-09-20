package com.app.edonymyeon.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import app.edonymyeon.BuildConfig.KAKAO_APP_KEY
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.service.client.RetrofitClient
import com.app.edonymyeon.data.util.PreferenceUtil
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EdonymyeonApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        PreferenceUtil.init(applicationContext)
        RetrofitClient.getInstance().updateAccessToken(
            PreferenceUtil.getValue(AuthLocalDataSource.USER_ACCESS_TOKEN),
        )
        KakaoSdk.init(this, KAKAO_APP_KEY)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
