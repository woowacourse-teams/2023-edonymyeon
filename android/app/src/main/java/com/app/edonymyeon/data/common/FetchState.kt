package com.app.edonymyeon.data.common

sealed class FetchState {
    object BadInternet : FetchState()
    object ParseError : FetchState()
    object WrongConnection : FetchState()

    object Fail : FetchState()
}
