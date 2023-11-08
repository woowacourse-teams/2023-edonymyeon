package com.app.edonymyeon.data.common

import com.app.edonymyeon.presentation.common.exception.HttpException

sealed interface FetchState {
    object BadInternet : FetchState
    object ParseError : FetchState
    object WrongConnection : FetchState

    class NoAuthorization(
        val customThrowable: HttpException,
    ) : FetchState

    class Fail(
        val customThrowable: HttpException,
    ) : FetchState
}
