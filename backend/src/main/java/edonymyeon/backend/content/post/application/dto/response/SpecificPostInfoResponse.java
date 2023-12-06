package edonymyeon.backend.content.post.application.dto.response;

import edonymyeon.backend.member.profile.domain.Member;
import edonymyeon.backend.content.post.domain.Post;
import java.time.LocalDateTime;
import java.util.List;

public record SpecificPostInfoResponse(
        long id,
        String title,
        long price,
        String content,
        LocalDateTime createdAt,
        List<String> images,
        WriterDetailResponse writer,
        ReactionCountResponse reactionCount,
        int upCount,
        int downCount,
        boolean isUp,
        boolean isDown,
        boolean isWriter
) {

    public static SpecificPostInfoResponse of(
            final Post post,
            final AllThumbsInPostResponse allThumbsInPost,
            final WriterDetailResponse writerDetailResponse,
            final ReactionCountResponse reactionCountResponse,
            final ThumbsStatusInPostResponse thumbsStatusInPost,
            final Member member,
            final String domain
    ) {
        return new SpecificPostInfoResponse(
                post.getId(),
                post.getTitle(),
                post.getPrice(),
                post.getContent(),
                post.getCreatedAt(),
                post.getPostImageInfos().stream().map(image -> domain + image.getStoreName()).toList(),
                writerDetailResponse,
                reactionCountResponse,
                allThumbsInPost.thumbsUpCount(),
                allThumbsInPost.thumbsDownCount(),
                thumbsStatusInPost.isUp(),
                thumbsStatusInPost.isDown(),
                post.isSameMember(member)
        );
    }

    public static SpecificPostInfoResponse of(
            final Post post,
            final AllThumbsInPostResponse allThumbsInPost,
            final WriterDetailResponse writerDetailResponse,
            final ReactionCountResponse reactionCountResponse,
            final String domain
    ) {
        return new SpecificPostInfoResponse(
                post.getId(),
                post.getTitle(),
                post.getPrice(),
                post.getContent(),
                post.getCreatedAt(),
                post.getPostImageInfos().stream().map(image -> domain + image.getStoreName()).toList(),
                writerDetailResponse,
                reactionCountResponse,
                allThumbsInPost.thumbsUpCount(),
                allThumbsInPost.thumbsDownCount(),
                false,
                false,
                false
        );
    }
}
