package edonymyeon.backend.comment.application.dto.response;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String image,
        String comment,
        boolean isWriter,
        LocalDateTime createdAt,
        WriterDto writer
) {

}
