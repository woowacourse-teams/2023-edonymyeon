package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.response.MyPostResponse
import com.app.edonymyeon.presentation.uimodel.DateUiModel
import com.app.edonymyeon.presentation.uimodel.MyPostUiModel
import com.domain.edonymyeon.model.MyPost

fun MyPostResponse.toDomain(): MyPost {
    return MyPost(
        id = id,
        title = title,
        image = image,
        content = content,
        createdAt = createdAt,
        consumption = consumption.toDomain(),
        reactionCount = reactionCount.toDomain(),
    )
}

fun MyPost.toUiModel(): MyPostUiModel {
    return MyPostUiModel(
        id = id,
        title = title,
        image = image,
        content = content,
        createdAt = DateUiModel(createdAt),
        consumption = consumption.toUiModel(),
        reactionCount = reactionCount.toUiModel(),
    )
}
