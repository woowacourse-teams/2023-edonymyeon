package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.service.client.calladapter.ApiException
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import okhttp3.Headers

fun <T, R> ApiResponse<T>.toResult(
    handleSuccess: ((T, Headers) -> R)? = null,
): Result<R> {
    return when (this) {
        is ApiResponse.Success -> {
            val resultData: R = handleSuccess?.invoke(data, headers) ?: data as R
            Result.success(resultData)
        }

        is ApiResponse.Failure -> {
            val exception = when (this) {
                is ApiResponse.Failure.NoAuthError -> ApiException.NoAuthException(
                    errorResponse.errorCode,
                    errorResponse.errorMessage,
                )

                is ApiResponse.Failure.HttpError -> ApiException.HttpError(
                    errorResponse.errorCode,
                    errorResponse.errorMessage,
                )

                is ApiResponse.Failure.UnknownApiError -> ApiException.UnknownApiError(throwable)
                is ApiResponse.Failure.NetworkError -> ApiException.NetworkError(throwable)
            }
            Result.failure(exception)
        }
    }
}
