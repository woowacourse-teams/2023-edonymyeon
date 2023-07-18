package edonymyeon.backend.post.application.dto;

import java.time.LocalDateTime;

public record PostFindingResponse(
        Long id,
        String title,
        String imagePath,
        String content,
        WriterResponse writer,
        LocalDateTime createdAt,
        int viewCount,
        int scrapCount,
        int commentCount
) {
}
