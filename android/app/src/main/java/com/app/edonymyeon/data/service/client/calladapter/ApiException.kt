package com.app.edonymyeon.data.service.client.calladapter

sealed class ApiException : Exception() {
    data class NoAuthException(val errorCode: Int, val errorMessage: String) : ApiException()
    data class HttpError(val errorCode: Int, val errorMessage: String) : ApiException()
    data class UnknownApiError(val throwable: Throwable) : ApiException()
    data class NetworkError(val throwable: Throwable) : ApiException()
}
