package com.app.edonymyeon.data.util

import com.app.edonymyeon.data.common.CustomThrowable
import retrofit2.Response

fun Response<Any>.toResult() {
    if (isSuccessful) {
        Result.success(Unit)
    } else {
        Result.failure(CustomThrowable(code(), message()))
    }
}
