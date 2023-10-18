package com.app.edonymyeon.data.common

import org.json.JSONObject
import retrofit2.Response

fun <T> createCustomThrowableFromResponse(result: Response<T>): CustomThrowable {
    val errorResponse = result.errorBody()?.string()
    val json = errorResponse?.let { JSONObject(it) }
    val errorMessage = json?.getString("errorMessage") ?: ""
    val errorCode = json?.getInt("errorCode") ?: 0
    return CustomThrowable(errorCode, errorMessage)
}
