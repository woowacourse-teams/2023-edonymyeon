package edonymyeon.backend.post.application.dto.response;

import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.post.domain.Post;
import java.time.LocalDateTime;

public record MyPostResponse(
        Long id,
        String title,
        String image,
        String content,
        LocalDateTime createdAt,
        PostConsumptionResponse consumption
) {

    public static MyPostResponse of(Post post, Domain domain, PostConsumptionResponse postConsumptionResponse) {
        return new MyPostResponse(
                post.getId(),
                post.getTitle(),
                post.getPostImageInfos().size() == 0 ? null
                        : domain.getDomain() + post.getPostImageInfos().get(0).getStoreName(),
                post.getContent(),
                post.getCreatedAt(),
                postConsumptionResponse
        );
    }
}
