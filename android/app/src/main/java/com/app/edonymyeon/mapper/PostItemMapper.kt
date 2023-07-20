package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.response.Post
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel
import com.app.edonymyeon.presentation.uimodel.ReactionCountUiModel
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
        reactionCount.viewCount,
        reactionCount.commentCount,
        reactionCount.scrapCount,
    ),
)

fun PostItem.toUiModel(): PostItemUiModel = PostItemUiModel(
    id = id,
    title = title,
    content = content,
    thumbnailUrl = image,
    createdAt = createdAt,
    nickname = nickname,
    reactionCount = ReactionCountUiModel(
        reactionCount.viewCount,
        reactionCount.commentCount,
        reactionCount.scrapCount,
    ),
)

//)
//id = id,
//    title = title,
//    thumbnailUrl = image,
//    content = content,
//    writer = WriterNickNameUiModel(writerNickNameDataModel.nickName),
//    createdAt = createdAt,
//    reactionCount = ReactionCountUiModel(
//        reactionCountDataModel.viewCount,
//        reactionCountDataModel.commentCount,
//        reactionCountDataModel.scrapCount,
//    ),