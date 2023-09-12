package edonymyeon.backend.post.application.dto.response;

import edonymyeon.backend.post.domain.Post;
import java.time.LocalDateTime;

public record GeneralPostInfoResponse(
        Long id,
        String title,
        String image,
        String content,
        WriterResponse writer,
        LocalDateTime createdAt,
        ReactionCountResponse reactionCount
) {

    public static GeneralPostInfoResponse of(Post post, String domain) {
        return new GeneralPostInfoResponse(
                post.getId(),
                post.getTitle(),
                post.hasThumbnail() ? domain + post.getThumbnailName() : null,
                post.getContent(),
                new WriterResponse(post.getMember().getNickname()),
                post.getCreatedAt(),
                new ReactionCountResponse(post.getViewCount(), post.getCommentCount())
        );
    }
}
