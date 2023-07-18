package com.app.edonymyeon.mapper

import com.app.edonymyeon.presentation.uimodel.RecommendationUiModel
import com.domain.edonymyeon.model.Count
import com.domain.edonymyeon.model.Recommendation

fun Recommendation.toUiModel(): RecommendationUiModel {
    return RecommendationUiModel(
        upCount = upCount.value,
        downCount = downCount.value,
        isUp = isUp,
        isDown = isDown,
        progress = progress,
    )
}

fun RecommendationUiModel.toDomain(): Recommendation {
    return Recommendation(
        upCount = Count(upCount),
        downCount = Count(downCount),
        isUp = isUp,
        isDown = isDown,
    )
}
