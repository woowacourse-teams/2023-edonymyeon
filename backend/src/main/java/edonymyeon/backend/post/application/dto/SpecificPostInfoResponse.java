package edonymyeon.backend.post.application.dto;

import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import java.time.LocalDateTime;
import java.util.List;

public record SpecificPostInfoResponse(
        long id,
        String title,
        long price,
        String content,
        LocalDateTime createdAt,
        List<PostImageInfo> imageUrls,
        WriterResponse writerResponse,
        ReactionCountResponse reactionCountResponse,
        int upCount,
        int downCount,
        boolean isUp,
        boolean isDown,
        boolean isScrap,
        boolean isWriter
) {
}
