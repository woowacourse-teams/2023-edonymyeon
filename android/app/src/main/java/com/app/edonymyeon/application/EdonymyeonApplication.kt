package com.app.edonymyeon.application

import android.app.Application
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.service.client.RetrofitClient
import com.app.edonymyeon.data.util.PreferenceUtil

class EdonymyeonApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        PreferenceUtil.init(applicationContext)
        RetrofitClient.getInstance().updateAccessToken(
            PreferenceUtil.getValue(AuthLocalDataSource.USER_ACCESS_TOKEN),
        )
    }
}
