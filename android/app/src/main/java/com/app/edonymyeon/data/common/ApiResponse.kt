package com.app.edonymyeon.data.common

sealed interface ApiResponse<out T> {
    data class Success<T>(val data: T, val headers: okhttp3.Headers) : ApiResponse<T>

    sealed interface Failure : ApiResponse<Nothing> {
        data class HttpError(
            val errorResponse: ErrorResponse,
        ) : Failure

        data class NoAuthError(
            val errorResponse: ErrorResponse,
        ) : Failure

        data class NetworkError(val throwable: Throwable) : Failure
        data class UnknownApiError(val throwable: Throwable) : Failure
    }
}

inline fun <T> ApiResponse<T>.onSuccess(
    action: (value: T) -> Unit,
): ApiResponse<T> {
    if (this is ApiResponse.Success) action(data)
    return this
}

inline fun <T> ApiResponse<T>.onFailure(
    action: (error: ApiResponse.Failure) -> Unit,
): ApiResponse<T> {
    if (this is ApiResponse.Failure) action(this)
    return this
}
