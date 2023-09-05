package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.CommentDataModel
import com.app.edonymyeon.presentation.uimodel.CommentDateUiModel
import com.app.edonymyeon.presentation.uimodel.CommentUiModel
import com.app.edonymyeon.presentation.uimodel.NicknameUiModel
import com.domain.edonymyeon.model.Comment

fun CommentDataModel.toUiModel(): CommentUiModel {
    return CommentUiModel(
        id = this.id,
        image = this.image,
        comment = this.comment,
        isWriter = this.isWriter,
        createdAt = CommentDateUiModel(this.createdAt),
        nicknameUiModel = NicknameUiModel(this.writerNickNameDataModel.nickName),
    )
}

fun CommentDataModel.toDomain(): Comment {
    return Comment(
        id = this.id,
        image = this.image,
        comment = this.comment,
        isWriter = this.isWriter,
        createdAt = this.createdAt,
        nickname = this.writerNickNameDataModel.nickName,
    )
}

fun Comment.toUiModel(): CommentUiModel {
    return CommentUiModel(
        id = this.id,
        image = this.image,
        comment = this.comment,
        isWriter = this.isWriter,
        createdAt = CommentDateUiModel(this.createdAt),
        nicknameUiModel = NicknameUiModel(this.nickname),
    )
}
