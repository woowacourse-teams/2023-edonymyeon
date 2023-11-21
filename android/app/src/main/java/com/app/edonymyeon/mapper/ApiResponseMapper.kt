package com.app.edonymyeon.mapper

import com.app.edonymyeon.presentation.common.exception.HttpException
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
                is ApiResponse.Failure.NoAuthError -> HttpException.NoAuthException(
                    errorResponse.errorCode,
                    errorResponse.errorMessage,
                )

                is ApiResponse.Failure.HttpError -> HttpException.HttpError(
                    errorResponse.errorCode,
                    errorResponse.errorMessage,
                )

                is ApiResponse.Failure.UnknownApiError -> HttpException.UnknownApiError(throwable)
                is ApiResponse.Failure.NetworkError -> HttpException.NetworkError(throwable)
            }
            Result.failure(exception)
        }
    }
}
