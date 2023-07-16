package com.app.edonymyeon.mapper

import com.app.edonymyeon.presentation.uimodel.ReactionCountUiModel
import com.domain.edonymyeon.model.ReactionCount

fun ReactionCount.toUiModel(): ReactionCountUiModel {
    return ReactionCountUiModel(
        viewCount = viewCount,
        commentCount = commentCount,
        scrapCount = scrapCount,
    )
}
