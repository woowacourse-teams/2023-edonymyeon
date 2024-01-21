package com.app.edonymyeon.presentation.common.exception

sealed class HttpException : Exception() {
    data class NoAuthException(val errorCode: Int, val errorMessage: String) : HttpException()
    data class HttpError(val errorCode: Int, val errorMessage: String) : HttpException()
    data class UnknownApiError(val throwable: Throwable) : HttpException()
    data class NetworkError(val throwable: Throwable) : HttpException()
}
