package com.app.edonymyeon.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WriterNickNameDataModel(@SerialName("nickname") val nickName: String)
