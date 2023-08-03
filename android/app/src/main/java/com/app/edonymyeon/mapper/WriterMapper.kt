package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.response.ProfileResponse
import com.app.edonymyeon.presentation.uimodel.WriterUiModel
import com.domain.edonymyeon.model.Writer

fun Writer.toUiModel(): WriterUiModel {
    return WriterUiModel(
        id = id,
        nickname = nickname,
        profileImage = profileImage,
    )
}

fun ProfileResponse.toDomain(): Writer {
    return Writer(
        id = id,
        nickname = nickname,
        profileImage = null,
    )
}
