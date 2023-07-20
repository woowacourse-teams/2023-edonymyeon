package edonymyeon.backend.post.application.dto;

import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.thumbs.dto.AllThumbsInPostResponse;
import edonymyeon.backend.thumbs.dto.ThumbsStatusInPostResponse;
import java.time.LocalDateTime;
import java.util.List;

public record SpecificPostInfoResponse(
        long id,
        String title,
        long price,
        String content,
        LocalDateTime createdAt,
        List<String> images,
        WriterDetailResponse writerDetailResponse,
        ReactionCountResponse reactionCountResponse,
        int upCount,
        int downCount,
        boolean isUp,
        boolean isDown,
        boolean isScrap,
        boolean isWriter
) {

    public static SpecificPostInfoResponse of(
            final Post post,
            final AllThumbsInPostResponse allThumbsInPost,
            final WriterDetailResponse writerDetailResponse,
            final ReactionCountResponse reactionCountResponse,
            final ThumbsStatusInPostResponse thumbsStatusInPost,
            final Member member
    ) {
        return new SpecificPostInfoResponse(
                post.getId(),
                post.getTitle(),
                post.getPrice(),
                post.getContent(),
                post.getCreatedAt(),
                post.getPostImageInfos().stream().map(ImageInfo::getUrl).toList(),
                writerDetailResponse,
                reactionCountResponse,
                allThumbsInPost.thumbsUpCount(),
                allThumbsInPost.thumbsDownCount(),
                thumbsStatusInPost.isUp(),
                thumbsStatusInPost.isDown(),
                false, // TODO: 스크랩 기능 구현 필요
                post.isSameMember(member)
        );
    }

    public static SpecificPostInfoResponse of(
            final Post post,
            final AllThumbsInPostResponse allThumbsInPost,
            final WriterDetailResponse writerDetailResponse,
            final ReactionCountResponse reactionCountResponse
    ) {
        return new SpecificPostInfoResponse(
                post.getId(),
                post.getTitle(),
                post.getPrice(),
                post.getContent(),
                post.getCreatedAt(),
                post.getPostImageInfos().stream().map(ImageInfo::getUrl).toList(),
                writerDetailResponse,
                reactionCountResponse,
                allThumbsInPost.thumbsUpCount(),
                allThumbsInPost.thumbsDownCount(),
                false,
                false,
                false, // TODO: 스크랩 기능 구현 필요
                false
        );
    }
}
