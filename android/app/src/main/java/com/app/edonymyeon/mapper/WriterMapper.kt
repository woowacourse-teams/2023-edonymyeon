package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.WriterDataModel
import com.app.edonymyeon.presentation.uimodel.NicknameUiModel
import com.app.edonymyeon.presentation.uimodel.WriterUiModel
import com.domain.edonymyeon.model.Writer

fun Writer.toUiModel(): WriterUiModel {
    return WriterUiModel(
        id = id,
        nickname = NicknameUiModel(nickname),
        profileImage = profileImage,
    )
}

fun WriterDataModel.toDomain(): Writer {
    return Writer(
        id = writerId,
        nickname = nickname,
        profileImage = null,
    )
}
