package com.app.edonymyeon.mapper

import com.app.edonymyeon.presentation.uimodel.RecommendationUiModel
import com.domain.edonymyeon.model.Recommendation

fun Recommendation.toUiModel(): RecommendationUiModel {
    return RecommendationUiModel(
        upCount = upCount,
        downCount = downCount,
        isUp = isUp,
        isDown = isDown,
        progress = progress,
    )
}
