package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.response.Post
import com.app.edonymyeon.presentation.uimodel.AllPostItemUiModel
import com.app.edonymyeon.presentation.uimodel.DateUiModel
import com.app.edonymyeon.presentation.uimodel.NicknameUiModel
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel
import com.app.edonymyeon.presentation.uimodel.ReactionCountUiModel
import com.domain.edonymyeon.model.Count
import com.domain.edonymyeon.model.PostItem
import com.domain.edonymyeon.model.ReactionCount

fun Post.toDomain(): PostItem = PostItem(
    id = id,
    title = title,
    image = image,
    content = content,
    createdAt = createdAt,
    nickname = writer.nickName,
    reactionCount = ReactionCount(
        Count(reactionCount.viewCount),
        Count(reactionCount.commentCount),
    ),
)

fun PostItem.toUiModel(): PostItemUiModel = PostItemUiModel(
    id = id,
    title = title,
    content = content,
    thumbnailUrl = image,
    createdAt = DateUiModel(createdAt),
    nickname = NicknameUiModel(nickname),
    reactionCount = ReactionCountUiModel(
        reactionCount.viewCount.value,
        reactionCount.commentCount.value,
    ),
)

fun PostItem.toAllPostItemUiModel(): AllPostItemUiModel = AllPostItemUiModel(
    id = id,
    title = title,
    content = content,
    nickname = NicknameUiModel(nickname),
    date = DateUiModel(createdAt),
    thumbnailUrl = image,
)
