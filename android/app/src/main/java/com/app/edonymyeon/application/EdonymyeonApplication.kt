package com.app.edonymyeon.application

import android.app.Application
import com.app.edonymyeon.data.util.PreferenceUtil

class EdonymyeonApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        PreferenceUtil.init(applicationContext)
    }
}
