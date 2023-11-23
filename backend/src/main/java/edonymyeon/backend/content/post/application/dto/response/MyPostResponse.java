package edonymyeon.backend.content.post.application.dto.response;

import edonymyeon.backend.content.post.domain.Post;
import java.time.LocalDateTime;

public record MyPostResponse(
        Long id,
        String title,
        String image,
        String content,
        LocalDateTime createdAt,
        PostConsumptionResponse consumption,
        ReactionCountResponse reactionCount
) {

    public static MyPostResponse of(Post post, String baseImageUrl, PostConsumptionResponse postConsumptionResponse) {
        return new MyPostResponse(
                post.getId(),
                post.getTitle(),
                post.hasThumbnail() ? baseImageUrl + post.getThumbnailName() : null,
                post.getContent(),
                post.getCreatedAt(),
                postConsumptionResponse,
                new ReactionCountResponse(post.getViewCount(), post.getCommentCount())
        );
    }
}
