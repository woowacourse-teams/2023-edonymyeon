package edonymyeon.backend.member.application.dto.response;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.post.domain.Post;
import java.time.LocalDateTime;

public record MyPostResponse(
        Long id,
        String title,
        String image,
        String content,
        LocalDateTime createdAt,
        ConsumptionResponse consumption
) {

    public static MyPostResponse of(Post post, Domain domain) {
        return new MyPostResponse(
                post.getId(),
                post.getTitle(),
                post.getPostImageInfos().size() == 0 ? null : domain.getDomain() + post.getPostImageInfos().get(0).getStoreName(),
                post.getContent(),
                post.getCreatedAt(),
                ConsumptionResponse.none()
        );
    }

    public static MyPostResponse of(Post post, Domain domain, Consumption consumption) {
        return new MyPostResponse(
                post.getId(),
                post.getTitle(),
                post.getPostImageInfos().size() == 0 ? null : domain.getDomain() + post.getPostImageInfos().get(0).getStoreName(),
                post.getContent(),
                post.getCreatedAt(),
                ConsumptionResponse.of(consumption)
        );
    }
}
