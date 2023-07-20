package com.app.edonymyeon.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionCountDataModel(
    val viewCount: Int,
    val commentCount: Int,
    val scrapCount: Int,
    @SerialName("viewCount") val viewCount: Int,
    @SerialName("commentCount") val commentCount: Int,
    @SerialName("scrapCount") val scrapCount: Int,
)
