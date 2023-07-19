package com.app.edonymyeon.mapper

import com.app.edonymyeon.presentation.uimodel.ReactionCountUiModel
import com.domain.edonymyeon.model.Count
import com.domain.edonymyeon.model.ReactionCount

fun ReactionCount.toUiModel(): ReactionCountUiModel {
    return ReactionCountUiModel(
        viewCount = viewCount.value,
        commentCount = commentCount.value,
        scrapCount = scrapCount.value,
    )
}

fun ReactionCountUiModel.toDomain(): ReactionCount {
    return ReactionCount(
        viewCount = Count(viewCount),
        commentCount = Count(commentCount),
        scrapCount = Count(scrapCount),
    )
}
