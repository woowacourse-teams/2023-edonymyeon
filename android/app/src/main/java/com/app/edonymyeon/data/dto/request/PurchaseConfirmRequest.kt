package com.app.edonymyeon.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseConfirmRequest(
    val purchasePrice: Int,
    val year: Int,
    val month: Int,
)
