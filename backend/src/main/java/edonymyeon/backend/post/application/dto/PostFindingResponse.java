package edonymyeon.backend.post.application.dto;

import edonymyeon.backend.post.domain.Post;
import java.time.LocalDateTime;

public record PostFindingResponse(
        Long id,
        String title,
        String image,
        String content,
        WriterResponse writer,
        LocalDateTime createdAt,
        int viewCount,
        int scrapCount,
        int commentCount
) {
    public static PostFindingResponse from(Post post) {
        return new PostFindingResponse(
                post.getId(),
                post.getTitle(),
                post.getPostImageInfos().size() == 0 ? null : post.getPostImageInfos().get(0).getFullPath(),
                post.getContent(),
                new WriterResponse(post.getMember().getNickname()),
                post.getCreateAt(),
                0, // TODO: 조회수
                0, // TODO: 스크랩 수
                0 // TODO: 댓글 수
        );
    }

}
