package com.app.edonymyeon.data.service.client

import app.edonymyeon.BuildConfig.APP_BASE_URL
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class RetrofitClient private constructor(private val baseUrl: String) {
    private val retrofit: Retrofit by lazy { provideRetrofit() }
    private val accessTokenInterceptor = AccessTokenInterceptor()

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://edonymyeon.site")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(OkHttpClient.provideOkHttpClient(accessTokenInterceptor))
            .build()
    }

    fun <T> create(service: Class<T>?): T {
        if (service == null) {
            throw RuntimeException("Api service is null!")
        }
        return retrofit.create(service)
    }

    fun updateAccessToken(token: String?) {
        accessTokenInterceptor.setToken(token)
    }

    fun clearAccessToken() {
        accessTokenInterceptor.clearToken()
    }

    companion object {
        private val contentType = "application/json".toMediaType()
        private var retrofitClient: RetrofitClient? = null

        fun getInstance(
            baseUrl: String = APP_BASE_URL,
        ): RetrofitClient {
            return retrofitClient ?: synchronized(this) {
                RetrofitClient(baseUrl).also {
                    retrofitClient = it
                }
            }
        }
    }
}
