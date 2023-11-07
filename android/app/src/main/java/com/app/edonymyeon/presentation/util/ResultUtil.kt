package com.app.edonymyeon.presentation.util

import com.app.edonymyeon.data.service.client.calladapter.ApiException

fun <T> Result<T>.onFailureWithApiException(onAction: (ApiException) -> Unit) {
    onFailure {
        onAction(it as ApiException)
    }
}
