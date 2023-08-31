package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.response.PostDetailResponse
import com.app.edonymyeon.presentation.uimodel.DateUiModel
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.domain.edonymyeon.model.Count
import com.domain.edonymyeon.model.Post
import com.domain.edonymyeon.model.ReactionCount
import com.domain.edonymyeon.model.Recommendation
import com.domain.edonymyeon.model.Writer
import java.time.LocalDateTime

fun Post.toUiModel(): PostUiModel {
    return PostUiModel(
        id = id,
        title = title,
        price = price,
        content = content,
        images = images,
        createdAt = DateUiModel(createdAt.toString()),
        writer = writer.toUiModel(),
        reactionCount = reactionCount.toUiModel(),
        recommendation = recommendation.toUiModel(),
        isScrap = isScrap,
        isWriter = isWriter,
    )
}

fun PostDetailResponse.toDomain(): Post {
    return Post(
        id = this.id,
        title = this.title,
        price = this.price,
        content = this.content,
        images = this.images,
        createdAt = LocalDateTime.parse(this.createdAt),
        writer = Writer(
            id = this.writer.writerId,
            nickname = this.writer.nickname,
            profileImage = this.writer.profileImage,
        ),
        reactionCount = ReactionCount(
            viewCount = Count(this.reactionCount.viewCount),
            commentCount = Count(this.reactionCount.commentCount),
        ),
        recommendation = Recommendation(
            upCount = Count(this.upCount),
            downCount = Count(this.downCount),
            isUp = this.isUp,
            isDown = this.isDown,
        ),
        isScrap = this.isScrap,
        isWriter = this.isWriter,
    )
}
