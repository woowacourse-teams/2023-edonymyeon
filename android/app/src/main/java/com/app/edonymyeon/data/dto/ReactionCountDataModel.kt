package com.app.edonymyeon.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReactionCountDataModel(
    val viewCount: Int,
    val commentCount: Int,
    val scrapCount: Int,
)
