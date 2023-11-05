package com.app.edonymyeon.data.common

sealed interface ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>

    sealed interface Failure : ApiResponse<Nothing> {
        data class HttpError(val code: Int, val message: String) : Failure
        data class NoAuthError(val code: Int, val message: String) : Failure
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
