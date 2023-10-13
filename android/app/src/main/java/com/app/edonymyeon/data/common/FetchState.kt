package com.app.edonymyeon.data.common

sealed interface FetchState {
    object BadInternet : FetchState
    object ParseError : FetchState
    object WrongConnection : FetchState

    class NoAuthorization(
        val customThrowable: CustomThrowable,
    ) : FetchState

    class Fail(
        val customThrowable: CustomThrowable,
    ) : FetchState
}
