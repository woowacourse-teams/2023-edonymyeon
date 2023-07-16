package com.app.edonymyeon.mapper

import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.domain.edonymyeon.model.Post

fun Post.toUiModel(): PostUiModel {
    return PostUiModel(
        id = id,
        title = title,
        price = price,
        content = content,
        images = images,
        createdAt = createdAt,
        writer = writer.toUiModel(),
        reactionCount = reactionCount.toUiModel(),
        recommendation = recommendation.toUiModel(),
        isScrap = isScrap,
        isWriter = isWriter,
    )
}
