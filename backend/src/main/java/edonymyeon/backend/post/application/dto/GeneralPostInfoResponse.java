package edonymyeon.backend.post.application.dto;

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
    public static GeneralPostInfoResponse from(Post post) {
        return new GeneralPostInfoResponse(
                post.getId(),
                post.getTitle(),
                post.getPostImageInfos().size() == 0 ? null : post.getPostImageInfos().get(0).getFullPath(),
                post.getContent(),
                new WriterResponse(post.getMember().getNickname()),
                post.getCreatedAt(),
                new ReactionCountResponse(0, 0, 0)
                // TODO: 조회수
                // TODO: 스크랩 수
                // TODO: 댓글 수
        );
    }

}
