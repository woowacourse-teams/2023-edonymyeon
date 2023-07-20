package edonymyeon.backend.post.application.dto;

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
}
