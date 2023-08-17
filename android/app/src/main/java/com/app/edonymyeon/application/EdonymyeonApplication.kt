package com.app.edonymyeon.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import app.edonymyeon.R
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.service.client.RetrofitClient
import com.app.edonymyeon.data.util.PreferenceUtil
import com.kakao.sdk.common.KakaoSdk

<<<<<<< HEAD
import app.edonymyeon.R

=======
import androidx.appcompat.app.AppCompatDelegate

>>>>>>> 0e9d5bc8a849f92eacf65f6d921aa7b4294a08dc
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.service.client.RetrofitClient
import com.app.edonymyeon.data.util.PreferenceUtil
import com.kakao.sdk.common.KakaoSdk

class EdonymyeonApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        PreferenceUtil.init(applicationContext)
        RetrofitClient.getInstance().updateAccessToken(
            PreferenceUtil.getValue(AuthLocalDataSource.USER_ACCESS_TOKEN),
        )
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
