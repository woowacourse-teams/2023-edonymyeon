package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SavingConfirmRequest(
    val year: Int,
    val month: Int,
)
