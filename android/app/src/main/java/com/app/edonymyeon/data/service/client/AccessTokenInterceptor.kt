package com.app.edonymyeon.data.service.client

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AccessTokenInterceptor : Interceptor {
    private var token: String? = null

    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val newRequest: Request = if (token != null) {
            request().newBuilder()
                .addHeader("Authorization", token!!)
                .build()
        } else {
            request()
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
