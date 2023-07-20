package com.app.edonymyeon.data.service.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class RetrofitClient private constructor(private val baseUrl: String) {
    private val retrofit: Retrofit by lazy { provideRetrofit() }
    private val accessTokenInterceptor = AccessTokenInterceptor()

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
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

    fun updateAccessToken(token: String) {
        accessTokenInterceptor.setToken(token)
    }

    fun clearAccessToken() {
        accessTokenInterceptor.clearToken()
    }

    companion object {
        private const val DEV_URL = "https://edonymyeon.site"
        private val contentType = "application/json".toMediaType()
        private var retrofitClient: RetrofitClient? = null

        fun getInstance(
            baseUrl: String = DEV_URL,
        ): RetrofitClient {
            return retrofitClient ?: synchronized(this) {
                RetrofitClient(baseUrl).also {
                    retrofitClient = it
                }
            }
        }
    }
}
