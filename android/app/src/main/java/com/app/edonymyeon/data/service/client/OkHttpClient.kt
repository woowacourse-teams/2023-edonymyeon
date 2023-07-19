package com.app.edonymyeon.data.service.client

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object OkHttpClient {
    fun provideOkHttpClient(
        interceptor: AccessTokenInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .run {
            addInterceptor(interceptor)
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            build()
        }
}


