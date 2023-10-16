package com.app.edonymyeon.data.common

sealed class Result<T> {

    class Success<T>(val data: T, val code: Int) : Result<T>()

    class ApiError<T>(val customThrowable: CustomThrowable) : Result<T>()

    class UnKnownApiError<T>(val code: Int) : Result<T>()

    class NetworkError<T>(val fetchState: FetchState) : Result<T>()

    class NullResult<T> : Result<T>()
}
