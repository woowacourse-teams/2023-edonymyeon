package com.app.edonymyeon.data.common

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
)
