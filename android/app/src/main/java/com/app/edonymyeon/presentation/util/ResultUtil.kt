package com.app.edonymyeon.presentation.util

import com.app.edonymyeon.presentation.common.exception.HttpException

fun <T> Result<T>.onFailureWithApiException(onAction: (HttpException) -> Unit) {
    onFailure {
        onAction(it as HttpException)
    }
}
