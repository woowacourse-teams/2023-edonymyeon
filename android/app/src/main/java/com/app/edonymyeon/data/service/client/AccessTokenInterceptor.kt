package com.app.edonymyeon.data.service.client

import app.edonymyeon.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

object AccessTokenInterceptor : Interceptor {
    private var token: String? = null
    private const val apiVersion = BuildConfig.VERSION_NAME

    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val newRequest: Request = if (token != null) {
            request().newBuilder()
                .addHeader("X-API-VERSION", apiVersion)
                .addHeader("Cookie", token!!)
                .build()
        } else {
            request().newBuilder()
                .addHeader("X-API-VERSION", apiVersion)
                .build()
        }
        proceed(newRequest)
    }

    fun setToken(token: String?) {
        this.token = token
    }

    fun clearToken() {
        this.token = null
    }
}
