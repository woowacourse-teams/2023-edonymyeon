package com.app.edonymyeon.data.service.client.calladapter

import com.google.gson.Gson
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

class EdonymyeonCall<T>(
    private val delegate: Call<T>,
    private val successType: Type,
) : Call<ApiResponse<T>> {
    override fun enqueue(callback: Callback<ApiResponse<T>>) = delegate.enqueue(
        object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(this@EdonymyeonCall, Response.success(response.toApiResult()))
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                val error = if (throwable is IOException) {
                    ApiResponse.Failure.NetworkError(throwable)
                } else {
                    ApiResponse.Failure.UnknownApiError(throwable)
                }
                callback.onResponse(this@EdonymyeonCall, Response.success(error))
            }
        },

    )

    private fun Response<T>.toApiResult(): ApiResponse<T> {
        if (!isSuccessful) {
            val errorBody =
                Gson().fromJson(errorBody()?.charStream(), ErrorResponse::class.java)
            if (errorBody.errorCode == ERROR_AUTHORIZATION_CODE) {
                return ApiResponse.Failure.NoAuthError(
                    errorBody,
                )
            }
            return ApiResponse.Failure.HttpError(
                errorBody,
            )
        }

        body()?.let { body ->
            return ApiResponse.Success(body, headers())
        }

        return if (successType == Unit::class.java) {
            @Suppress("UNCHECKED_CAST")
            (
                ApiResponse.Success(
                    Unit as T,
                    headers(),
                )
                )
        } else {
            ApiResponse.Failure.UnknownApiError(
                IllegalStateException(
                    ERROR_BODY_NOT_EXIST,
                ),
            )
        }
    }

    override fun clone(): Call<ApiResponse<T>> = EdonymyeonCall(delegate.clone(), successType)

    override fun execute(): Response<ApiResponse<T>> {
        throw UnsupportedOperationException(ERROR_UNSUPPORTED_EXCEPTION)
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()

    companion object {
        private const val ERROR_UNSUPPORTED_EXCEPTION = "EdonymyeonCall doesn't support"
        private const val ERROR_BODY_NOT_EXIST =
            "Body doesn't exist, must be declared ApiResponse<Unit>"
        private const val ERROR_AUTHORIZATION_CODE = 1523
    }
}
