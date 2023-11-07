package com.app.edonymyeon.data.common

import com.app.edonymyeon.data.service.client.calladapter.ApiException

sealed interface FetchState {
    object BadInternet : FetchState
    object ParseError : FetchState
    object WrongConnection : FetchState

    class NoAuthorization(
        val customThrowable: ApiException,
    ) : FetchState

    class Fail(
        val customThrowable: ApiException,
    ) : FetchState
}
